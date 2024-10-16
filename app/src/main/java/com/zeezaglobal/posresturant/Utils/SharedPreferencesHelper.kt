package com.zeezaglobal.posresturant.Utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zeezaglobal.posresturant.Entities.CartItem
import com.zeezaglobal.posresturant.Entities.Item

class SharedPreferencesHelper (private val context: Context) {

    private val sharedPrefFile = "com.zeezaglobal.posresturant.PREFERENCE_FILE"
    private val sharedPref: SharedPreferences =
        context.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sharedPref.edit()

    // Save cart item to SharedPreferences
    fun saveCartItemToSharedPreferences(item: Item) {
        val existingItemsJson = sharedPref.getString("cartItemList", null)

        // Convert JSON back to a List<CartItem> or initialize a new list
        val cartItemType = object : TypeToken<MutableList<CartItem>>() {}.type
        val cartItemList: MutableList<CartItem> = if (existingItemsJson != null) {
            Gson().fromJson(existingItemsJson, cartItemType)
        } else {
            mutableListOf()
        }

        // Check if the item is already in the cart
        val existingCartItem = cartItemList.find { it.item.itemId == item.itemId }
        if (existingCartItem != null) {
            existingCartItem.quantity += 1
        } else {
            cartItemList.add(CartItem(item = item, quantity = 1))
        }

        // Convert the updated list back to JSON and store it
        val updatedCartItemsJson = Gson().toJson(cartItemList)
        editor.putString("cartItemList", updatedCartItemsJson)
        editor.apply()
    } // Load cart items from SharedPreferences
    fun loadCartFromSharedPreferences(): MutableList<CartItem> {
        val itemsJson = sharedPref.getString("cartItemList", null)
        return if (itemsJson != null) {
            val cartItemType = object : TypeToken<MutableList<CartItem>>() {}.type
            Gson().fromJson(itemsJson, cartItemType)
        } else {
            mutableListOf()
        }
    }

    // Clear the cart in SharedPreferences
    fun clearCart() {
        editor.remove("cartItemList")
        editor.apply()
    }

    // Clear all shared preferences (if needed)
    fun clearAllPreferences() {
        editor.clear()
        editor.apply()
    }
}