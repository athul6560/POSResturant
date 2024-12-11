package com.zeezaglobal.posresturant.Utils

import java.text.SimpleDateFormat
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
}