package com.zeezaglobal.posresturant.WorkManager

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.zeezaglobal.posresturant.Application.POSApp
import com.zeezaglobal.posresturant.Dao.SaleDao
import com.zeezaglobal.posresturant.Database.POSDatabase
import com.zeezaglobal.posresturant.Entities.Sale
import com.zeezaglobal.posresturant.Retrofit.ApiService
import com.zeezaglobal.posresturant.Retrofit.RetrofitInstance
import com.zeezaglobal.posresturant.Retrofit.data.SaleItem
import com.zeezaglobal.posresturant.Retrofit.data.SaleRequest

class SaleSyncWorker(
    context: Context,
    workerParams: WorkerParameters,

) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val saleDao = POSApp.instance.database.saleDao()
        // get API
        val saleApi = RetrofitInstance.api
        val unsyncedSales = saleDao.getUnsyncedSales()

        unsyncedSales.forEach { sale ->
            try {
                val response = saleApi.createSale(saleMapper(sale)).execute()
                if (response.isSuccessful) {
                    saleDao.editSale(sale.copy(syncStatus = true))
                } else {
                    return Result.retry()
                }
            } catch (e: Exception) {
                return Result.retry()
            }
        }

        return Result.success()
    }
}

private fun SaleSyncWorker.saleMapper(sale: Sale): SaleRequest {

    val saleItems = sale.items.map { cartItem ->
        SaleItem(
            itemId = cartItem.item.itemId, // assuming your Item class has an `id` field
            quantity = cartItem.quantity
        )
    }

    return SaleRequest(
        billNumber = sale.billNumber,
        tokenNumber = sale.tokenNumber,
        status = sale.status,
        store = sale.store,
        totalAmount = sale.totalAmount,
        dateTime = sale.dateTime,
        paymentMethod = sale.paymentMethod,
        customerName = sale.customerName,
        customerEmail = sale.customerEmail,
        customerPhone = sale.customerPhone,
        items = saleItems
    )
}
