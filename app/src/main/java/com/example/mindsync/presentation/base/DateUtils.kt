package com.example.mindsync.presentation.base


import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object DateUtils {

    fun compareTwoDates(endDate: Calendar, startDate: Calendar): Boolean {
        return when {
            endDate.before(startDate) -> true
            endDate.after(startDate) -> false
            else -> false
        }
    }

    fun checkTwoDatesAreEqual(startDate: Calendar, endDate: Calendar): Boolean {
        return (
            startDate[Calendar.YEAR] == endDate[Calendar.YEAR] &&
                startDate[Calendar.MONTH] == endDate[Calendar.MONTH] &&
                startDate[Calendar.DAY_OF_MONTH] == endDate[Calendar.DAY_OF_MONTH]
            )
    }

    fun getTimeFormatString(date: Long): String {
        val month = String.format("%tb", date)
        val day = String.format(Locale.getDefault(), "%td", date)
        val time = SimpleDateFormat("hh:mm", Locale.getDefault()).format(date)
        val decimalMark = String.format("%tp", date).uppercase(Locale.getDefault())
        val value = "$day $month, $time$decimalMark"
        return value
    }

    fun getTimeFormatTextInHHMM(date: Long): String {
        val time = SimpleDateFormat("hh:mm", Locale.getDefault()).format(date)
        return time
    }

    fun convertDate(date: String): String {
        var dateString = ""
        try {
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS Z", Locale.ROOT)
            val output = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS Z", Locale.ROOT)
            dateString = sdf.parse(date)?.let { output.format(it) }.toString()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return dateString
    }
}
