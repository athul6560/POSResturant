package com.zeezaglobal.posresturant.Dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.zeezaglobal.posresturant.Entities.Sale

@Dao
interface SaleDao {

    @Insert
     fun insertSale(sale: Sale)

    @Query("SELECT * FROM sales")
    suspend fun getAllSales(): List<Sale>
}