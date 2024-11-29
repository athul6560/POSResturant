package com.zeezaglobal.posresturant.Repository

import com.zeezaglobal.posresturant.Dao.ItemDao
import com.zeezaglobal.posresturant.Dao.SaleDao
import com.zeezaglobal.posresturant.Entities.Item
import com.zeezaglobal.posresturant.Entities.Sale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SaleRepository (private val saleDao: SaleDao) {

    // Insert a new item
    suspend fun insertSale(item: Sale) = withContext(Dispatchers.IO) {
        saleDao.insertSale(item)
    }

 

    suspend fun getAllSale() = withContext(Dispatchers.IO) {
        saleDao.getAllSales()
    }
}