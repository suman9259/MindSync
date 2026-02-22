package com.example.mindsync.data.worker

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.mindsync.domain.model.DayOfWeek
import com.example.mindsync.domain.model.Reminder
import com.example.mindsync.domain.model.RepeatType
import java.util.Calendar
import java.util.concurrent.TimeUnit

class ReminderScheduler(private val context: Context) {

    private val workManager = WorkManager.getInstance(context)

    fun scheduleReminder(reminder: Reminder) {
        val initialDelay = calculateInitialDelay(reminder.scheduledTime)
        
        val data = Data.Builder()
            .putString(ReminderWorker.KEY_TITLE, reminder.title)
            .putString(ReminderWorker.KEY_MESSAGE, reminder.description.ifEmpty { reminder.notes })
            .putString(ReminderWorker.KEY_TYPE, reminder.type.name.lowercase())
            .build()

        when (reminder.repeatType) {
            RepeatType.ONCE -> scheduleOneTimeReminder(reminder.id, initialDelay, data)
            RepeatType.DAILY -> scheduleDailyReminder(reminder.id, initialDelay, data)
            RepeatType.WEEKLY -> scheduleWeeklyReminder(reminder.id, initialDelay, data, reminder.repeatDays)
            RepeatType.CUSTOM -> scheduleCustomReminder(reminder.id, initialDelay, data, reminder.repeatDays)
        }
    }

    private fun scheduleOneTimeReminder(id: String, delay: Long, data: Data) {
        val request = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .addTag(id)
            .build()

        workManager.enqueue(request)
    }

    private fun scheduleDailyReminder(id: String, delay: Long, data: Data) {
        val request = PeriodicWorkRequestBuilder<ReminderWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .addTag(id)
            .build()

        workManager.enqueueUniquePeriodicWork(
            id,
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }

    private fun scheduleWeeklyReminder(id: String, delay: Long, data: Data, days: List<DayOfWeek>) {
        val request = PeriodicWorkRequestBuilder<ReminderWorker>(7, TimeUnit.DAYS)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .addTag(id)
            .build()

        workManager.enqueueUniquePeriodicWork(
            id,
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }

    private fun scheduleCustomReminder(id: String, delay: Long, data: Data, days: List<DayOfWeek>) {
        days.forEach { day ->
            val dayDelay = calculateDelayForDay(delay, day)
            val request = PeriodicWorkRequestBuilder<ReminderWorker>(7, TimeUnit.DAYS)
                .setInitialDelay(dayDelay, TimeUnit.MILLISECONDS)
                .setInputData(data)
                .addTag("${id}_${day.name}")
                .build()

            workManager.enqueueUniquePeriodicWork(
                "${id}_${day.name}",
                ExistingPeriodicWorkPolicy.UPDATE,
                request
            )
        }
    }

    fun cancelReminder(id: String) {
        workManager.cancelAllWorkByTag(id)
        DayOfWeek.entries.forEach { day ->
            workManager.cancelAllWorkByTag("${id}_${day.name}")
        }
    }

    private fun calculateInitialDelay(scheduledTime: Long): Long {
        val now = System.currentTimeMillis()
        val targetCalendar = Calendar.getInstance().apply {
            timeInMillis = scheduledTime
        }
        
        val todayTarget = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, targetCalendar.get(Calendar.HOUR_OF_DAY))
            set(Calendar.MINUTE, targetCalendar.get(Calendar.MINUTE))
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        return if (todayTarget.timeInMillis > now) {
            todayTarget.timeInMillis - now
        } else {
            todayTarget.add(Calendar.DAY_OF_YEAR, 1)
            todayTarget.timeInMillis - now
        }
    }

    private fun calculateDelayForDay(baseDelay: Long, targetDay: DayOfWeek): Long {
        val calendar = Calendar.getInstance()
        val currentDay = calendar.get(Calendar.DAY_OF_WEEK)
        val targetDayValue = when (targetDay) {
            DayOfWeek.SUNDAY -> Calendar.SUNDAY
            DayOfWeek.MONDAY -> Calendar.MONDAY
            DayOfWeek.TUESDAY -> Calendar.TUESDAY
            DayOfWeek.WEDNESDAY -> Calendar.WEDNESDAY
            DayOfWeek.THURSDAY -> Calendar.THURSDAY
            DayOfWeek.FRIDAY -> Calendar.FRIDAY
            DayOfWeek.SATURDAY -> Calendar.SATURDAY
        }

        val daysUntilTarget = (targetDayValue - currentDay + 7) % 7
        return baseDelay + (daysUntilTarget * 24 * 60 * 60 * 1000L)
    }
}
