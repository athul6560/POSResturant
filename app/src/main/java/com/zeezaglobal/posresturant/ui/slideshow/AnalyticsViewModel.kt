package com.zeezaglobal.posresturant.ui.slideshow

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zeezaglobal.posresturant.Entities.Sale
import com.zeezaglobal.posresturant.Repository.SaleRepository
import kotlinx.coroutines.launch
import java.util.Date

class AnalyticsViewModel (private val saleRepository: SaleRepository) : ViewModel() {

    // LiveData to hold the list of sales
    private val _sales = MutableLiveData<List<Sale>>()
    val sales: LiveData<List<Sale>> get() = _sales

    fun fetchSalesForDate(date: Date) {
        viewModelScope.launch {
            try {
                val salesList = saleRepository.getSalesByDateRange(date)
                _sales.postValue(salesList)
            } catch (e: Exception) {
                // Handle error (e.g., log it, show error state in UI)
                _sales.postValue(emptyList())
            }
        }
    }
}