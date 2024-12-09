package com.zeezaglobal.posresturant.ui.slideshow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.zeezaglobal.posresturant.Repository.SaleRepository

class SaleViewModelFactory(private val saleRepository: SaleRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AnalyticsViewModel::class.java)) {
            return AnalyticsViewModel(saleRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}