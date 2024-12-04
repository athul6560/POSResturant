package com.zeezaglobal.posresturant.ui.EditMenu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.zeezaglobal.posresturant.Repository.GroupRepository
import com.zeezaglobal.posresturant.Repository.ItemRepository
import com.zeezaglobal.posresturant.ViewModel.EditMenuViewModel

class EditMenuViewModelFactory(
    private val groupRepository: GroupRepository,
    private val itemRepository: ItemRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditMenuViewModel::class.java)) {
            return EditMenuViewModel(groupRepository, itemRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}