package com.zeezaglobal.posresturant.Utils

import android.content.Context
import java.text.SimpleDateFormat
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
    fun generateToken(): Int {
        val currentDate = getCurrentDate()
        val savedDate = sharedPreferences.getString("lastTokenDate", "") ?: ""
        var tokenNumber = sharedPreferences.getInt("lastTokenNumber", 0)

        if (currentDate != savedDate) {
            // Reset the token if the day has changed
            tokenNumber = 1
            saveTokenData(currentDate, tokenNumber)
        } else {
            // Increment the token number for the current day
            tokenNumber++
            saveTokenData(currentDate, tokenNumber)
        }

        return tokenNumber
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