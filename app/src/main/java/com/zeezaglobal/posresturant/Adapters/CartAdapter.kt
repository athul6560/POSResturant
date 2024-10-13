package com.zeezaglobal.posresturant.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zeezaglobal.posresturant.Entities.Item
import com.zeezaglobal.posresturant.R

class CartAdapter (private var itemList: List<Item>) : RecyclerView.Adapter<CartAdapter.ItemViewHolder>() {

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemName: TextView = itemView.findViewById(R.id.item_name_text_view)
        val itemDescription: TextView = itemView.findViewById(R.id.item_description_text_view)
        val itemPrice: TextView = itemView.findViewById(R.id.item_price_text_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_list_row, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = itemList[position]
        holder.itemName.text = item.itemName
        holder.itemDescription.text = item.itemDescription
        holder.itemPrice.text = "₹${item.itemPrice}" // Format price as needed
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    fun updateItems(newItems: List<Item>) {
        itemList = newItems
        notifyDataSetChanged() // Notify the adapter of data changes
    }}