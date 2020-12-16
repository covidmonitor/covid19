package pl.covid19.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import pl.covid19.database.CovidDatabase.Companion.getInstance
import pl.covid19.repository.CovidRepository
import retrofit2.HttpException

class RefreshDataWorker(appContext: Context, params: WorkerParameters):
        CoroutineWorker(appContext, params) {

    val context =appContext
    companion object {
        const val WORK_NAME = "RefreshDataWorker"
    }
    /**
     * A coroutine-friendly method to do your work.
     */
    override suspend fun doWork(): Result {
        val database = getInstance(applicationContext)
        val repository = CovidRepository(database, context)
        if (true)//atPlRange()) TODO 12 chceck is needed to check
            return try {
                    repository.refresh()
                Result.success()
            } catch (e: HttpException) {
                Result.retry()
            }
        else {
            return Result.success()
        }
    }
}
