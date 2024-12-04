package com.zeezaglobal.posresturant.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zeezaglobal.posresturant.Entities.Group
import com.zeezaglobal.posresturant.Entities.Item
import com.zeezaglobal.posresturant.Repository.GroupRepository
import com.zeezaglobal.posresturant.Repository.ItemRepository
import kotlinx.coroutines.launch

class EditMenuViewModel(private val groupRepository: GroupRepository,
                        private val itemRepository: ItemRepository
) : ViewModel(){

    private val _items = MutableLiveData<List<Item>>()
    val items: LiveData<List<Item>> get() = _items

    fun loadItemsByGroup(groupId: Int) {
        viewModelScope.launch {
            val itemList = itemRepository.getItemsByGroup(groupId)
            _items.postValue(itemList)
        }
    }

    // Function to edit/update an item
    fun editItem(item: Item) {
        viewModelScope.launch {
            itemRepository.updateItem(item)
            // Optionally, reload items after the update
            loadItemsByGroup(item.groupId)
        }
    }
}