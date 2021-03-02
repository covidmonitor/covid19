
package pl.covid19.repository


import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import kotlinx.coroutines.*
import pl.covid19.CovidMonitorApplication
import pl.covid19.MainActivity
import pl.covid19.R
import pl.covid19.database.CovidDatabase
import pl.covid19.network.*
import pl.covid19.util.Constants.CHANNEL_ID_PERIOD_WORK
import pl.covid19.util.TodayToStringSql
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*


class CovidRepository(private val database: CovidDatabase, ctx: Context) {


    /**
     * A single network request, the results won't change. For this lesson we did not add an offline cache for simplicity
     * and the result will be cached in memory.
     */
    //private val request = localizationService.getGusAll()

    /**
     * An in-progress (or potentially completed) find location, this may be null or cancelled at any time.
     * If this is non-null, calling await will get the result of the last location  request.
     * This will be cancelled whenever location changes, as the old results are no longer valid.
     */
    //private var inProgressSort: Deferred<SortedData>? = null
    /*private var inProgressSort: Deferred<Boolean>? = null

    var isFullyInitialized = false
        private set*/

    val context = ctx
    val DevIDShort=CovidMonitorApplication.Variables.DevIDShort
    /**
     * Refresh the  stored in the offline cache.
     * This function uses the IO dispatcher to ensure the database insert database operation
     * happens on the IO dispatcher. By switching to the IO dispatcher using `withContext` this
     * function is now safe to call from any thread including the Main thread.
     */
    suspend fun refresh(all:Boolean=false) {
        val handler = CoroutineExceptionHandler { _, exception ->
            Timber.i("Work Caught $exception")
        }
        //withContext
        Timber.i("Work refresh +" +all.toString())
        if (all)
            CoroutineScope(Dispatchers.IO).launch(handler) {
                getGovplx()
                getFazyVersionDB()
                showNotification(context.getString(R.string.reinserted_rows, "Od 01-12-2020 do " + TodayToStringSql(0)))
            }
        else
        CoroutineScope(Dispatchers.IO).launch(handler) {
            if (database.covidDao.getCountGovplx()==0)  getGovplx()
            else
            {
                if (database.covidDao.getCountGovplxDay(TodayToStringSql(0))==0) {
                    val tmpIns= getGovplxDate(TodayToStringSql(0))
                    if (tmpIns != null) {
                            getFazyVersionDB()
                          //   val tmp2=tmpIns.filterIndexed {_, i->tmpIns[i.toInt()] !=-1L }
                            if (tmpIns.size >0)
                                showNotification(context.getString(R.string.inserted_rows,  TodayToStringSql(0)))
                            }
                }
            }
            if (database.covidDao.getCountFazy()==0) {
                getFazyVersionDB(false)
                showNotification(context.getString(R.string.inserted_rows, "Od 01-12-2020 do " + TodayToStringSql(0)))
            }
        }
    }

    suspend private fun getGovplx(all:Boolean=true) {
        val     pllist = Network.govplxList.getAll(DevIDShort)
        val plContainer = NetworkGovplxContainer(pllist)
        if (all) database.covidDao.clearGovplx()
        database.covidDao.insertGovlxAll(*plContainer.asDatabaseModel())
    }
    suspend private fun getGovplxDate(date: String):LongArray? {
        val pllist = Network.govplxList.getbyDate(date,DevIDShort)
        val plContainer = NetworkGovplxContainer(pllist)
        return database.covidDao.insertGovlxAll(*plContainer.asDatabaseModel())
    }

    suspend private fun getFazyVersionDB(all:Boolean=true) {
        val fazy = Network.fazyList.getAll(DevIDShort)
        val fazyContainer = NetworkFazyContainer(fazy)
        if (all) database.covidDao.clearFazy()
        database.covidDao.insertFazyAll(*fazyContainer.asDatabaseModel())

        val version = Network.versionList.getAll(DevIDShort)
        val versionContainer = NetworkVersionContainer(version)
        if (all)  database.covidDao.clearVersion()
        database.covidDao.insertVersionAll(*versionContainer.asDatabaseModel())
    }

    private fun showNotification(description: String) {
        val now = Date()
        val id: Int = SimpleDateFormat("ddHHmmss", Locale.GERMAN).format(now).toInt()

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = TaskStackBuilder.create(context).run {
            // Add the intent, which inflates the back stack
            addNextIntentWithParentStack(intent)
            // Get the PendingIntent containing the entire back stack
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val vibrate = longArrayOf(100, 200, 300, 400, 500)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notification = NotificationCompat.Builder(context, CHANNEL_ID_PERIOD_WORK).apply {
                setContentIntent(pendingIntent)}
            notification.setContentTitle(context.getString(R.string.inserted_string))
            notification.setContentText(description)
            notification.priority = NotificationCompat.PRIORITY_HIGH
            notification.setCategory(NotificationCompat.CATEGORY_SERVICE)
            notification.setSmallIcon(R.drawable.ic_home_white_24dp)
            notification.setNumber(1)
            notification.setSound(sound)
            notification.setVibrate(vibrate)

            with(NotificationManagerCompat.from(context)) {
                notify(id, notification.build())}
        }
        else{
            val notification = NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_home_white_24dp)
                .setContentTitle(context.getString(R.string.inserted_string))
                .setContentText(description)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setNumber(1)
                .setContentIntent(pendingIntent)
                .setAutoCancel(false)
                .setSound(sound)
                .setVibrate(vibrate)

            val notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(id, notification.build())
        }
    }

/*
    /**
     * Call when location changes.
     * This will cancel any previous queries, so it's important to re-request the data after calling this function
     * @param location the location to set id in govplx
     */
    suspend fun onLocationChanged(location: Location) {
        // We need to ensure we're on Dispatchers.Main so that this is not running on multiple Dispatchers and we
        // modify the member inProgressSort.

        // Since this was called from viewModelScope, that will always be a simple if check (not expensive), but
        // by specifying the dispatcher we can protect against incorrect usage.
        withContext(Dispatchers.Main) {
            isFullyInitialized = true

            // cancel any in progress sorts, their result is not valid anymore.
            inProgressSort?.cancel()

            doGetGUS(location)
        }
    }

    /**
     * Call this to force a new IDGus  to start.
     *
     * This will start a new coroutine to perform the find out IDGus. Future requests to find out IDGus data can use the deferred in
     * [inProgressSort] to get the result of the last sort without sorting the data again. This guards against multiple
     * find out IDGus  being performed on the same data, which is inefficient.
     * This will always cancel if the location changes while the sort is in progress.
     * @return the result find out IDGus
     */
    private suspend fun doGetGUS(location: Location? = null): findedData {
        // since we'll need to launch a new coroutine for the find out IDGus  use coroutineScope.
        // coroutineScope will automatically wait for anything started via async {} or await{} in it's block to
        // complete.
        val result = coroutineScope {
            // launch a new coroutine to  find out IDGus(so other requests can wait for this find out IDGus  to complete)
            val deferred =  null //async { findedData.from(request.await(), location) }
            // cache the Deferred so any future requests can wait for this sort
            inProgressSort = deferred
            // and return the result of this sort
            //deferred.await()
        }
        return  findedData() //result
    }

    /**
     * Holds data sorted by the distance from the last location.
     *
     * Note, by convention this class won't sort on the Main thread. This is not a public API and should
     * only be called by [doSortData].
     */
    private class findedData internal constructor(
    ) {

        companion object {
            /**
             * Sort the data from a [GdgResponse] by the specified location.
             *
             * @param response the response to sort
             * @param location the location to sort by, if null the data will not be sorted.
             */
            suspend fun from(/*response: GdgResponse,*/ location: Location?): findedData {
                return withContext(Dispatchers.Default) {
                    // this sorting is too expensive to do on the main thread, so do thread confinement here.
                    //val chapters: List<GdgChapter> = response.chapters.sortByDistanceFrom(location)
                    // use distinctBy which will maintain the input order - this will have the effect of making
                    // a filter list sorted by the distance from the current location
                    //val filters: List<String> = chapters.map { it.region } .distinctBy { it }
                    // group the chapters by region so that filter queries don't require any work
                    //val chaptersByRegion: Map<String, List<GdgChapter>> = chapters.groupBy { it.region }
                    // return the sorted result
                    //SortedData(chapters, filters, chaptersByRegion)
                    findedData()
                }

            }


            /**
             * Sort a list of GdgChapter by their distance from the specified location.
             *
             * @param currentLocation returned list will be sorted by the distance, or unsorted if null
             */
            /*private fun List<GdgChapter>.sortByDistanceFrom(currentLocation: Location?): List<GdgChapter> {
                currentLocation ?: return this

                return sortedBy { distanceBetween(it.geo, currentLocation)}
            }*/

            /**
             * Calculate the distance (in meters) between a LatLong and a Location.
             */
            /*private fun distanceBetween(start: LatLong, currentLocation: Location): Float {
                val results = FloatArray(3)
                Location.distanceBetween(start.lat, start.long, currentLocation.latitude, currentLocation.longitude, results)
                return results[0]
            }*/
        }
    }
*/

}
