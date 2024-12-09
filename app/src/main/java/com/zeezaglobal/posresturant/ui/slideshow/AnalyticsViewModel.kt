package com.zeezaglobal.posresturant.ui.slideshow

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zeezaglobal.posresturant.Entities.Sale
import com.zeezaglobal.posresturant.Repository.SaleRepository
import kotlinx.coroutines.launch

class AnalyticsViewModel (private val saleRepository: SaleRepository) : ViewModel() {

    // LiveData to hold the list of sales
    private val _sales = MutableLiveData<List<Sale>>()
    val sales: LiveData<List<Sale>> get() = _sales

    // Function to load all sales
    fun fetchAllSales() {
        viewModelScope.launch {
            try {
                val salesList = saleRepository.getAllSale()
                _sales.postValue(salesList)
            } catch (e: Exception) {
                // Handle error (e.g., log it, show error state in UI)
                _sales.postValue(emptyList())
            }
        }
    }
}