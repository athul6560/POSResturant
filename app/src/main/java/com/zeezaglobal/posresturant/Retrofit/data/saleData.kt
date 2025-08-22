package com.zeezaglobal.posresturant.Retrofit.data

data class SaleItem(
    val itemId: Int,
    val quantity: Int
)

data class SaleRequest(
    val billNumber: Long,
    val tokenNumber: Int,
    val status: Int,
    val totalAmount: Double,
    val dateTime: String,
    val paymentMethod: String,
    val customerName: String?,
    val customerEmail: String?,
    val customerPhone: String?,
    val items: List<SaleItem>
)

data class SaleResponse(
    val message: String // assuming your API returns a simple success message
)