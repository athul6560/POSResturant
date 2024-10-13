package com.zeezaglobal.posresturant.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zeezaglobal.posresturant.Entities.Item
import com.zeezaglobal.posresturant.R

class GridAdapter(private var items: List<Item>) : RecyclerView.Adapter<GridAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_grid, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.textViewItemName.text = item.itemName
        holder.textViewItemDescription.text = item.itemDescription
        holder.textViewItemPrice.text = String.format("₹%.2f", item.itemPrice) // Formatting price as currency
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewItemName: TextView = itemView.findViewById(R.id.textViewItemName)
        val textViewItemDescription: TextView = itemView.findViewById(R.id.textViewItemDescription)
        val textViewItemPrice: TextView = itemView.findViewById(R.id.textViewItemPrice)
    }
    fun updateItems(newItems: List<Item>) {
        items = newItems
        notifyDataSetChanged() // Notify the adapter of data changes
    }
}