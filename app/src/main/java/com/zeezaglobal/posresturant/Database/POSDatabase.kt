package com.zeezaglobal.posresturant.Database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.zeezaglobal.posresturant.Dao.GroupDao
import com.zeezaglobal.posresturant.Dao.ItemDao
import com.zeezaglobal.posresturant.Dao.SaleDao
import com.zeezaglobal.posresturant.Entities.CartItemListConverter
import com.zeezaglobal.posresturant.Entities.Group
import com.zeezaglobal.posresturant.Entities.Item
import com.zeezaglobal.posresturant.Entities.Sale

@Database(entities = [Group::class, Item::class, Sale::class], version = 1)
@TypeConverters(CartItemListConverter::class)
abstract class POSDatabase : RoomDatabase() {
    abstract fun groupDao(): GroupDao
    abstract fun itemDao(): ItemDao
    abstract fun saleDao(): SaleDao
}