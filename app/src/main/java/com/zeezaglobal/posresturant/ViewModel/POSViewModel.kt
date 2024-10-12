package com.zeezaglobal.posresturant.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zeezaglobal.posresturant.Entities.Group
import com.zeezaglobal.posresturant.Entities.Item
import com.zeezaglobal.posresturant.Repository.GroupRepository
import com.zeezaglobal.posresturant.Repository.ItemRepository
import kotlinx.coroutines.launch

class POSViewModel(
    private val groupRepository: GroupRepository,
    private val itemRepository: ItemRepository
) : ViewModel() {

    // Insert a group
    fun addGroup(groupName: String) {
        val group = Group(groupName = groupName)
        viewModelScope.launch {
            groupRepository.insertGroup(group)
        }
    }

    // Get all groups
    fun loadGroups() {
        viewModelScope.launch {
            val groups = groupRepository.getAllGroups()
            // Do something with the groups (e.g., update UI)
        }
    }

    // Add an item to a group
    fun addItemToGroup(groupId: Int, itemName: String, itemDescription: String, itemPrice: Double) {
        val item = Item(groupId = groupId, itemName = itemName, itemDescription = itemDescription, itemPrice = itemPrice)
        viewModelScope.launch {
            itemRepository.insertItem(item)
        }
    }

    // Get items by group
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
}