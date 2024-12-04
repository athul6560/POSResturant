package com.zeezaglobal.posresturant.Repository

import com.zeezaglobal.posresturant.Dao.ItemDao
import com.zeezaglobal.posresturant.Entities.Item
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ItemRepository(private val itemDao: ItemDao) {

    // Insert a new item
    suspend fun insertItem(item: Item) = withContext(Dispatchers.IO) {
        itemDao.insertItem(item)
    }

    // Get all items in a specific group
    suspend fun getItemsByGroup(groupId: Int): List<Item> = withContext(Dispatchers.IO) {
        itemDao.getItemsByGroup(groupId)
    }

    // Delete a specific item by ID
    suspend fun deleteItem(itemId: Int) = withContext(Dispatchers.IO) {
        itemDao.deleteItem(itemId)
    }

    suspend fun getAllItem() = withContext(Dispatchers.IO) {
        itemDao.getAllItems()
    }
    suspend fun updateItem(item: Item) = withContext(Dispatchers.IO) {
        itemDao.updateItem(item)
    }
}