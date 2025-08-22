package com.zeezaglobal.posresturant.Repository

import android.content.Context
import android.util.Log
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.zeezaglobal.posresturant.Dao.SaleDao
import com.zeezaglobal.posresturant.Entities.Sale
import com.zeezaglobal.posresturant.WorkManager.SaleSyncWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.Executors

class SaleRepository(private val saleDao: SaleDao) {

    private val executor = Executors.newSingleThreadExecutor()

    fun insertSale(item: Sale, context: Context) {
        executor.execute {
            try {
                saleDao.insertSale(item)
                syncSales(context)
            } catch (e: Exception) {
                Log.e("SaleRepository", "Error inserting sale: ${e.message}")

            }
        }
    }
    suspend fun editSale(sale: Sale) {
        try {
            withContext(Dispatchers.IO) {
                saleDao.editSale(sale)
            }
        } catch (e: Exception) {
            Log.e("SaleRepository", "Error updating sale: ${e.message}")
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

private fun SaleRepository.syncSales(context: Context) {
    val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    val workRequest = OneTimeWorkRequest.Builder(SaleSyncWorker::class.java)
        .setConstraints(constraints)
        .build()

    WorkManager.getInstance(context).enqueue(workRequest)
}
