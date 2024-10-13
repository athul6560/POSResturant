package com.zeezaglobal.posresturant.Dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.zeezaglobal.posresturant.Entities.Item

@Dao
interface ItemDao {

    @Insert
    suspend fun insertItem(item: Item)

    @Query("SELECT * FROM item_table WHERE groupId = :groupId")
    suspend fun getItemsByGroup(groupId: Int): List<Item>

    @Query("DELETE FROM item_table WHERE itemId = :itemId")
    suspend fun deleteItem(itemId: Int)

    @Query("SELECT * FROM item_table") // New method to get all items
    suspend fun getAllItems(): List<Item>
}