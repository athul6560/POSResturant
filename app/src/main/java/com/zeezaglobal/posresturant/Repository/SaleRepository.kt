package com.zeezaglobal.posresturant.Repository

import com.zeezaglobal.posresturant.Dao.SaleDao
import com.zeezaglobal.posresturant.Entities.Sale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors

class SaleRepository (private val saleDao: SaleDao) {

    private val executor = Executors.newSingleThreadExecutor()

    fun insertSale(item: Sale) {
        executor.execute {
            saleDao.insertSale(item)
        }
    }

 

    suspend fun getAllSale() = withContext(Dispatchers.IO) {
        saleDao.getAllSales()
    }
}