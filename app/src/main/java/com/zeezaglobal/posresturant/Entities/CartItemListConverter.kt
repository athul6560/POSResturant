package com.zeezaglobal.posresturant.Entities

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class CartItemListConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromCartItemList(cartItems: List<CartItem>?): String {
        return gson.toJson(cartItems) // Serialize the list to JSON
    }

    @TypeConverter
    fun toCartItemList(cartItemsString: String?): List<CartItem> {
        if (cartItemsString.isNullOrEmpty()) return emptyList() // Handle null or empty JSON
        val listType = object : TypeToken<List<CartItem>>() {}.type
        return gson.fromJson(cartItemsString, listType) // Deserialize JSON back to list
    }
}