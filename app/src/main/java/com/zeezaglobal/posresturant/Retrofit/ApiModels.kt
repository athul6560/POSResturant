package com.zeezaglobal.posresturant.Retrofit

data class GroupResponse(
    val groupId: Int,
    val groupName: String,
    val items: List<Item>
)

data class Item(
    val itemId: Int,
    val itemName: String,
    val itemDescription: String,
    val itemPrice: Double
)