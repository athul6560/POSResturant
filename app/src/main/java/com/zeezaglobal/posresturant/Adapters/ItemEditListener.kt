package com.zeezaglobal.posresturant.Adapters

import com.zeezaglobal.posresturant.Entities.Item

interface ItemEditListener {
    fun onEditItem(item: Item, position: Int)
}