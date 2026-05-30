package com.zeezaglobal.posresturant.ViewModel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zeezaglobal.posresturant.Entities.Group
import com.zeezaglobal.posresturant.Entities.Item
import com.zeezaglobal.posresturant.Repository.GroupRepository
import com.zeezaglobal.posresturant.Repository.ItemRepository
import com.zeezaglobal.posresturant.Utils.ExcelParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddNewViewModel(
    private val groupRepository: GroupRepository,
    private val itemRepository: ItemRepository
) : ViewModel() {
    private val _groups = MutableLiveData<List<Group>>()
    private val _items = MutableLiveData<List<Item>>()
    val groups: LiveData<List<Group>> get() = _groups
    val items: LiveData<List<Item>> get() = _items

    init {
        loadGroups()
        loadItems()
    }
    // Insert a group
    fun addGroup(groupName: String) {
        val group = Group(groupName = groupName)
        viewModelScope.launch {
            groupRepository.insertGroup(group)
            loadGroups()
        }
    }
    fun loadItems() {
        viewModelScope.launch {
            viewModelScope.launch {
                val itemList = itemRepository.getAllItem()
                _items.value = itemList
            }
        }
    }
    // Get all groups
    fun loadGroups() {
        viewModelScope.launch {
            viewModelScope.launch {
                val groupList = groupRepository.getAllGroups()
                _groups.value = groupList
            }
        }
    }

    // Add an item to a group
    fun addItemToGroup(groupId: Int, itemName: String, itemDescription: String, itemPrice: Double) {
        val item = Item(groupId = groupId, itemName = itemName, itemDescription = itemDescription, itemPrice = itemPrice)
        viewModelScope.launch {
            itemRepository.insertItem(item)
            loadItems()
        }
    }

    // Get items by group jh
    fun loadItemsForGroup(groupId: Int) {
        viewModelScope.launch {
            val items = itemRepository.getItemsByGroup(groupId)
            // Do something with the items (e.g., update UI)
        }
    }

    // Delete a group by ID
    fun deleteGroup(groupId: Int) {
        viewModelScope.launch {
            groupRepository.deleteGroup(groupId)
        }
    }

    // Delete an item by ID
    fun deleteItem(itemId: Int) {
        viewModelScope.launch {
            itemRepository.deleteItem(itemId)
        }
    }

    // Import groups and items from an Excel file (.xlsx)
    // Each non-empty size column value creates a separate item: "Item Name - Size"
    fun importFromExcel(context: Context, uri: Uri, onComplete: (itemCount: Int) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val menuMap = ExcelParser.parse(context, uri)
                if (menuMap.isEmpty()) throw Exception("No data found — check column headers (CATEGORY, ITEM NAME)")

                // Clear existing data then re-insert
                groupRepository.deleteAll()

                var totalItems = 0
                menuMap.forEach { (categoryName, itemList) ->
                    val groupId = groupRepository.insertAndGetId(Group(groupName = categoryName)).toInt()
                    itemList.forEach { (name, price) ->
                        itemRepository.insertItem(
                            Item(groupId = groupId, itemName = name, itemDescription = "", itemPrice = price)
                        )
                        totalItems++
                    }
                }

                withContext(Dispatchers.Main) {
                    loadGroups()
                    loadItems()
                    onComplete(totalItems)
                }
            } catch (e: Exception) {
                Log.e("AddNewViewModel", "Excel import failed: ${e.message}", e)
                withContext(Dispatchers.Main) { onComplete(-1) }
            }
        }
    }
}