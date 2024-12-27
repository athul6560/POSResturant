package com.zeezaglobal.posresturant.Repository

import android.util.Log
import com.zeezaglobal.posresturant.Dao.SaleDao
import com.zeezaglobal.posresturant.Entities.Sale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.Executors

class SaleRepository(private val saleDao: SaleDao) {

    private val executor = Executors.newSingleThreadExecutor()

    fun insertSale(item: Sale) {
        executor.execute {
            try {
                saleDao.insertSale(item)
            } catch (e: Exception) {
                Log.e("SaleRepository", "Error inserting sale: ${e.message}")

            }
        }
    }

    suspend fun getSalesByDateRange(startTime:String, endTime:String): List<Sale> {

        Log.d("TAG", "getSalesByDateRange: $startTime $endTime")
        return saleDao.getSalesByDateRange(startTime, endTime)
    }



    suspend fun getAllSale() = withContext(Dispatchers.IO) {
        saleDao.getAllSales()
    }
}