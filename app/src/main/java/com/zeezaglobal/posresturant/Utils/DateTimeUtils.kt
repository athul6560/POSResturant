package com.zeezaglobal.posresturant.Utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.Locale.*

object DateTimeUtils {

    // Method to get the current date
    fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("dd MMMM, yyyy", getDefault())
        return dateFormat.format(Date())
    }

    // Method to get the current time
    fun getCurrentTime(): String {
        val timeFormat = SimpleDateFormat("HH:mm:ss", getDefault())
        return timeFormat.format(Date())
    }
     fun getStartAndEndOfRangeOfDates(startDate: Date, endDate: Date): Pair<Date, Date> {
        val calendar = Calendar.getInstance()

        // Calculate the start of the range (3 AM of the startDate)
        calendar.time = startDate
        calendar.set(Calendar.HOUR_OF_DAY, 3)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        // If the startDate's time is before 3 AM, adjust to the previous day
        if (startDate.before(calendar.time)) {
            calendar.add(Calendar.DAY_OF_YEAR, -1)
        }
        val rangeStart = calendar.time

        // Calculate the end of the range (3 AM of the day after endDate)
        calendar.time = endDate
        calendar.set(Calendar.HOUR_OF_DAY, 3)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        // Move to 3 AM of the next day
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        val rangeEnd = calendar.time

        return Pair(rangeStart, rangeEnd)
    }
    fun getStartAndEndOfDay(date: Date): Pair<Date, Date> {
        val calendar = Calendar.getInstance()
        calendar.time = date

        // If the current time is before 3 AM, we need to adjust the start and end times
        if (calendar.get(Calendar.HOUR_OF_DAY) < 3) {
            // Set to 3 AM of the previous day (start of the previous day)
            calendar.add(Calendar.DAY_OF_YEAR, -1)  // Move to the previous day
            calendar.set(Calendar.HOUR_OF_DAY, 3)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            val startOfDay = calendar.time

            // Set to 3 AM of the current day (end of the previous day)
            calendar.add(Calendar.DAY_OF_YEAR, 1)  // Move back to the current day
            val endOfDay = calendar.time

            return Pair(startOfDay, endOfDay)
        } else {
            // Set to 3 AM of the current day (start of the day)
            calendar.set(Calendar.HOUR_OF_DAY, 3)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            val startOfDay = calendar.time

            // Set to 3 AM of the next day (end of the day)
            calendar.add(Calendar.DAY_OF_YEAR, 1)
            val endOfDay = calendar.time

            return Pair(startOfDay, endOfDay)
        }
    }
}