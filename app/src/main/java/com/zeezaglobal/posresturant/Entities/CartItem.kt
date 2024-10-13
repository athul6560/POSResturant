package com.zeezaglobal.posresturant.Entities

data class CartItem(
    val item: Item,    // The actual item details (you might already have an Item class)
    var quantity: Int  // The number of items added to the cart
)