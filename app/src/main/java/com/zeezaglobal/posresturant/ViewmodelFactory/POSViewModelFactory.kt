package com.zeezaglobal.posresturant.ViewmodelFactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.zeezaglobal.posresturant.Repository.GroupRepository
import com.zeezaglobal.posresturant.Repository.ItemRepository
import com.zeezaglobal.posresturant.ViewModel.AddNewViewModel

class POSViewModelFactory(
    private val groupRepository: GroupRepository,
    private val itemRepository: ItemRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddNewViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddNewViewModel(groupRepository, itemRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}