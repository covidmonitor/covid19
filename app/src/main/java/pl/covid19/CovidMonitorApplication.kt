package pl.covid19

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.os.Build
import androidx.work.*
import com.cod3rboy.crashbottomsheet.CrashBottomSheet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pl.covid19.util.Constants.CHANNEL_ID_ONE_TIME_WORK
import pl.covid19.util.Constants.CHANNEL_ID_PERIOD_WORK
import pl.covid19.util.Constants.WORK_TAG
import pl.covid19.work.RefreshDataWorker
import timber.log.Timber
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates


class CovidMonitorApplication : Application() {
    object Variables {
        var isNetworkConnected: Boolean by Delegates.observable(false) { property, oldValue, newValue ->
            Timber.i("Network connectivity" +"$newValue")
        }
    }
    val applicationScope = CoroutineScope(Dispatchers.Default)

    private fun delayedInit() {
        applicationScope.launch {
            setupRecurringWork()
        }
    }

    private fun setupRecurringWork() {
        val constraints = Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .setRequiredNetworkType(NetworkType.CONNECTED)
                //.setRequiredNetworkType(NetworkType.UNMETERED)
                .apply {
                    setRequiresDeviceIdle(false)
                }
                .build()
        val repeatingRequest
                = PeriodicWorkRequestBuilder<RefreshDataWorker>(15, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .addTag(WORK_TAG)
                .build()

        WorkManager.getInstance().enqueueUniquePeriodicWork(
            RefreshDataWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.REPLACE,
            repeatingRequest)
    }

    /**
     * onCreate is called before the first screen is shown to the user.
     *
     * Use it to setup any background tasks, running expensive setup operations in a background
     * thread to avoid delaying app start.
     */
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        createNotificationChannel()
        startNetworkCallback()
        delayedInit()
        CrashBottomSheet.register(this);
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channelPeriodic = NotificationChannel(CHANNEL_ID_PERIOD_WORK,
                "Period Work Request",
                importance)
            channelPeriodic.description = "Periodic Work"
            val channelInstant = NotificationChannel(CHANNEL_ID_ONE_TIME_WORK,
                "One Time Work Request",
                importance)
            channelInstant.description  = "One Time Work"
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = applicationContext.getSystemService(NotificationManager::class.java)
            notificationManager!!.createNotificationChannel(channelPeriodic)
            notificationManager.createNotificationChannel(channelInstant)
        }
    }

    fun startNetworkCallback() {

        val cm: ConnectivityManager =
            this.getSystemService(Context.CONNECTIVITY_SERVICE)    as ConnectivityManager
        val builder: NetworkRequest.Builder = NetworkRequest.Builder()

        cm.registerNetworkCallback(
            builder.build(),
            object : ConnectivityManager.NetworkCallback() {

                override fun onAvailable(network: Network) {
                    Variables.isNetworkConnected = true
                }

                override fun onLost(network: Network) {
                    Variables.isNetworkConnected = false
                }
            })
    }

}
