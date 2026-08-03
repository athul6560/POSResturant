package com.zeezaglobal.posresturant.Database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.zeezaglobal.posresturant.Dao.GroupDao
import com.zeezaglobal.posresturant.Dao.ItemDao
import com.zeezaglobal.posresturant.Dao.SaleDao
import com.zeezaglobal.posresturant.Entities.CartItemListConverter
import com.zeezaglobal.posresturant.Entities.Group
import com.zeezaglobal.posresturant.Entities.Item
import com.zeezaglobal.posresturant.Entities.Sale

@Database(entities = [Group::class, Item::class, Sale::class], version = 2)
@TypeConverters(CartItemListConverter::class)
abstract class POSDatabase : RoomDatabase() {
    abstract fun groupDao(): GroupDao
    abstract fun itemDao(): ItemDao
    abstract fun saleDao(): SaleDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE item_table ADD COLUMN imagePath TEXT")
            }
        }
    }
}