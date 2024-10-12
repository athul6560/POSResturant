package com.zeezaglobal.posresturant.Database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.zeezaglobal.posresturant.Dao.GroupDao
import com.zeezaglobal.posresturant.Dao.ItemDao
import com.zeezaglobal.posresturant.Entities.Group
import com.zeezaglobal.posresturant.Entities.Item

@Database(entities = [Group::class, Item::class], version = 1)
abstract class POSDatabase : RoomDatabase() {
    abstract fun groupDao(): GroupDao
    abstract fun itemDao(): ItemDao
}