package com.zeezaglobal.posresturant.Dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.zeezaglobal.posresturant.Entities.Sale

@Dao
interface SaleDao {

    @Insert
     fun insertSale(sale: Sale)

    @Query("SELECT * FROM sales")
    suspend fun getAllSales(): List<Sale>

    @Query("SELECT * FROM sales WHERE dateTime >= :startTime AND dateTime < :endTime")
    suspend fun getSalesByDateRange(startTime: String, endTime: String): List<Sale>

    @Update
    suspend fun editSale(sale: Sale)
}