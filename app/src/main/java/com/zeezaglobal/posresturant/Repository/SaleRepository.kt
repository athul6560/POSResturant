package com.zeezaglobal.posresturant.Repository

import com.zeezaglobal.posresturant.Dao.SaleDao
import com.zeezaglobal.posresturant.Entities.Sale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.Executors

class SaleRepository (private val saleDao: SaleDao) {

    private val executor = Executors.newSingleThreadExecutor()

    fun insertSale(item: Sale) {
        executor.execute {
            saleDao.insertSale(item)
        }
    }
    suspend fun getSalesByDateRange(date: Date): List<Sale> {
        val (startOfDay, endOfDay) = getStartAndEndOfDay(date)

        val startTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(startOfDay)
        val endTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(endOfDay)

        return saleDao.getSalesByDateRange(startTime, endTime)
    }

    private fun getStartAndEndOfDay(date: Date): Pair<Date, Date> {
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

    suspend fun getAllSale() = withContext(Dispatchers.IO) {
        saleDao.getAllSales()
    }
}