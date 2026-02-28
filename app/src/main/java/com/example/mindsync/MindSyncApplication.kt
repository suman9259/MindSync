package com.example.mindsync

import android.app.Application
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.mindsync.data.worker.DailyResetWorker
import com.example.mindsync.di.appModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import java.util.Calendar
import java.util.concurrent.TimeUnit

class MindSyncApplication : Application() {
    private val applicationScope = CoroutineScope(Dispatchers.IO)
    
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@MindSyncApplication)
            modules(appModule)
        }
        
        scheduleDailyResetWorker()
        
        // Check and reset on app startup if date has changed
        applicationScope.launch {
            try {
                DailyResetWorker.performDailyReset(this@MindSyncApplication)
            } catch (e: Exception) {
                // Silently fail - worker will handle it later
            }
        }
    }
    
    private fun scheduleDailyResetWorker() {
        val currentTime = Calendar.getInstance()
        val midnight = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            add(Calendar.DAY_OF_YEAR, 1)
        }
        
        val initialDelay = midnight.timeInMillis - currentTime.timeInMillis
        
        val dailyResetRequest = PeriodicWorkRequestBuilder<DailyResetWorker>(
            1, TimeUnit.DAYS
        )
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .build()
        
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            DailyResetWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            dailyResetRequest
        )
    }
}
