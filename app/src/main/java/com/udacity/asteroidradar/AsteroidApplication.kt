package com.udacity.asteroidradar

import android.app.Application
import android.os.Build
import androidx.work.*
import com.udacity.asteroidradar.work.RefreshDataWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class AsteroidApplication : Application() {

    val applicationScope = CoroutineScope(Dispatchers.Default)

    //Create an initialization function that does not block the main thread:
    //It's important to note that WorkManager.initialize should be called from inside onCreate without
    // using a background thread to avoid issues caused when initialization happens after WorkManager is used.

    override fun onCreate() {
        super.onCreate()
        delayedInt()
    }

    //put non-essential setup code in this function to avoid blocking the first screen
    private fun delayedInt() {
        applicationScope.launch {
            setupRecurringWork()
        }
    }

    //setup WorkManager background job to fetch new network daily
    private fun setupRecurringWork() {
        //Define constraints to prevent work from occurring when there is no network access or the device is low on battery.
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .setRequiresBatteryNotLow(true)
            .setRequiresCharging(true)
            .apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    setRequiresDeviceIdle(true)
                }
            }.build()


        val repeatingRequest =
            PeriodicWorkRequestBuilder<RefreshDataWorker>(1, TimeUnit.DAYS).setConstraints(
                constraints
            ).build()

        WorkManager.getInstance().enqueueUniquePeriodicWork(
            RefreshDataWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            repeatingRequest
        )
    }


}