package com.zeezaglobal.posresturant.Utils

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import kotlin.random.Random

class BillandTocken (context: Context){
    // SharedPreferences to store token data
    private val sharedPreferences = context.getSharedPreferences("BillTokenPrefs", Context.MODE_PRIVATE)
    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    /**
     * Generates a daily recurring token number.
     * Resets to 1 every new day.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun generateToken(): Int {
        val currentDateTime = getCurrentDateTime() // Function to get the current date and time
        val currentDate = currentDateTime.toLocalDate() // Extract the date part
        val currentTime = currentDateTime.toLocalTime() // Extract the time part

        val savedDate = sharedPreferences.getString("lastTokenDate", "") ?: ""
        val savedTokenNumber = sharedPreferences.getInt("lastTokenNumber", 0)
        var tokenNumber = savedTokenNumber

        // Check if the current time is past 6 AM or we're still on the last saved day
        val savedDateTime = LocalDateTime.parse(
            sharedPreferences.getString("lastResetDateTime", "1970-01-01T06:00:00"),
            DateTimeFormatter.ISO_LOCAL_DATE_TIME
        )
        val lastResetDate = savedDateTime.toLocalDate()
        val lastResetTime = savedDateTime.toLocalTime()

        if (currentDate.isAfter(lastResetDate) ||
            (currentDate == lastResetDate && currentTime.isAfter(LocalTime.of(6, 0))) &&
            lastResetTime.isBefore(LocalTime.of(6, 0))) {
            // Reset if it's a new day after 6 AM or the saved time is before 6 AM
            tokenNumber = 1
            saveTokenData(currentDateTime, tokenNumber)
        } else {
            // Increment for the current day
            tokenNumber++
            saveTokenData(savedDateTime, tokenNumber)
        }

        return tokenNumber
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun saveTokenData(dateTime: LocalDateTime, tokenNumber: Int) {
        with(sharedPreferences.edit()) {
            putString("lastTokenDate", dateTime.toLocalDate().toString())
            putInt("lastTokenNumber", tokenNumber)
            putString("lastResetDateTime", dateTime.toString())
            apply()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getCurrentDateTime(): LocalDateTime {
        return LocalDateTime.now() // Adjust timezone if needed
    }

    /**
     * Generates a globally unique bill number.
     * Format: "YYYYMMDDHHMMSS_<RANDOM>"
     */
    fun generateUniqueBillNumber(): Long {
        val timestamp = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(Date())
        val randomComponent = Random.nextInt(1000, 9999)

        // Combine timestamp and random component as a string, then convert to Long
        val billNumberString = timestamp + randomComponent.toString()

        // Convert the concatenated string to a Long
        return billNumberString.toLong()
    }

    // Get today's date as a string
    private fun getCurrentDate(): String {
        return dateFormatter.format(Date())
    }

    // Save the current token data to SharedPreferences
    private fun saveTokenData(date: String, token: Int) {
        sharedPreferences.edit()
            .putString("lastTokenDate", date)
            .putInt("lastTokenNumber", token)
            .apply()
    }
}