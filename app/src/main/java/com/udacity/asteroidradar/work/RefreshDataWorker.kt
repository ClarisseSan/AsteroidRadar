package com.udacity.asteroidradar.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.network.AsteroidApiFilter
import com.udacity.asteroidradar.repository.AsteroidRepository
import retrofit2.HttpException

//We're going to use a CoroutineWorker,
// because we want to use coroutines to handle our asynchronous code and threading
class RefreshDataWorker(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {
    companion object {
        const val WORK_NAME = "RefreshDataWorker"
    }

    override suspend fun doWork(): Result {
        val database = getDatabase(applicationContext)
        val repository = AsteroidRepository(database)

        return try {
            repository.refreshAsteroid()
            Result.success()
        } catch (e: HttpException) {
            Result.retry()
        }
    }
}