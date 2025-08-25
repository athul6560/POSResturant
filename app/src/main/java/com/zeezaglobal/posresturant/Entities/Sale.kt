package com.zeezaglobal.posresturant.Entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Entity(tableName = "sales",indices = [androidx.room.Index(value = ["billNumber"], unique = true)])
data class Sale(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0, // Unique ID for each sale
    val billNumber: Long,
    val tokenNumber: Int,
    var status: Int,
    var store: Int,
    val totalAmount: Double,
    val dateTime: String,
    val paymentMethod: String,
    @TypeConverters(CartItemListConverter::class) // Use CartItemListConverter
    val items: List<CartItem>,

    // Optional fields for customer information
    var customerName: String? = null,
    val customerEmail: String? = null,
    val customerPhone: String? = null,

    val syncStatus: Boolean = false

)
