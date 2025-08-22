package com.zeezaglobal.posresturant.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.zeezaglobal.posresturant.Repository.ApiRepository
import com.zeezaglobal.posresturant.Retrofit.GroupResponse
import kotlinx.coroutines.launch

class LoadingViewmodel(private val repository: ApiRepository) : ViewModel() {

    private val _groups = MutableLiveData<List<GroupResponse>>()
    val groups: LiveData<List<GroupResponse>> get() = _groups

    fun addDatatoRoom() {
        viewModelScope.launch {
            val data = repository.getGroups()
            data?.let {
                _groups.postValue(it)
            }
        }
    }
}

class GroupViewModelFactory(private val repository: ApiRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoadingViewmodel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoadingViewmodel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}