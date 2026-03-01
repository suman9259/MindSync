package com.example.mindsync.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.mindsync.data.local.UserPreferences
import com.example.mindsync.data.local.database.MindSyncDatabase
import com.example.mindsync.data.local.database.entity.DailyProgressEntity
import com.example.mindsync.data.local.security.SecurePreferences
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DailyResetWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            performDailyReset(applicationContext)
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    companion object {
        const val WORK_NAME = "daily_reset_worker"

        suspend fun performDailyReset(context: Context) {
            val userPreferences = UserPreferences(context)
            val securePreferences = SecurePreferences(context)
            val database = MindSyncDatabase.getDatabase(context, securePreferences)

            val medicineDao = database.medicineDao()
            val skincareDao = database.skincareDao()
            val dailyProgressDao = database.dailyProgressDao()

            val todayString = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().time)
            val lastResetDate = userPreferences.lastResetDate.first()

            // Only reset if we haven't reset today
            if (lastResetDate == todayString) {
                return
            }

            // Get yesterday's date for saving progress
            val yesterday = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, -1)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val yesterdayString = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(yesterday.time)

            // Get all unique user IDs from medicines and skincare
            val medicines = medicineDao.getUnsyncedMedicines()
            val skincareRoutines = skincareDao.getUnsyncedRoutines()
            val userIds = (medicines.map { it.userId } + skincareRoutines.map { it.userId }).distinct()

            for (userId in userIds) {
                // Save yesterday's progress before resetting
                saveProgressForUser(
                    userId = userId,
                    dateString = yesterdayString,
                    date = yesterday.timeInMillis,
                    medicineDao = medicineDao,
                    skincareDao = skincareDao,
                    dailyProgressDao = dailyProgressDao
                )
            }

            // Reset daily status for all users
            medicineDao.resetDailyStatus()
            skincareDao.resetDailyStatus()

            // Mark today as reset
            userPreferences.saveLastResetDate(todayString)
        }

        private suspend fun saveProgressForUser(
            userId: String,
            dateString: String,
            date: Long,
            medicineDao: com.example.mindsync.data.local.database.dao.MedicineDao,
            skincareDao: com.example.mindsync.data.local.database.dao.SkincareDao,
            dailyProgressDao: com.example.mindsync.data.local.database.dao.DailyProgressDao
        ) {
            val progressId = "${userId}_$dateString"

            // Check if progress already exists for this date
            val existingProgress = dailyProgressDao.getProgressById(progressId)
            if (existingProgress != null) {
                return // Already saved
            }

            // Get medicine stats
            val medicinesTaken = medicineDao.getTakenTodayCount(userId)
            val medicinesTotal = medicineDao.getActiveMedicineCount(userId)

            // Get skincare stats
            val skincareCompleted = skincareDao.getCompletedTodayCount(userId)
            val skincareTotal = skincareDao.getRoutineCount(userId)

            // Calculate totals
            val totalCompleted = medicinesTaken + skincareCompleted
            val totalTasks = medicinesTotal + skincareTotal

            val progress = DailyProgressEntity(
                id = progressId,
                userId = userId,
                date = date,
                dateString = dateString,
                medicinesTaken = medicinesTaken,
                medicinesTotal = medicinesTotal,
                skincareRoutinesCompleted = skincareCompleted,
                skincareRoutinesTotal = skincareTotal,
                totalTasksCompleted = totalCompleted,
                totalTasks = totalTasks
            )

            dailyProgressDao.insertProgress(progress)
        }
    }
}
