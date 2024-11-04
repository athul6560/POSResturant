package com.zeezaglobal.posresturant.Adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zeezaglobal.posresturant.Entities.Item
import com.zeezaglobal.posresturant.R

class GridAdapter(
    private var itemList: List<Item>,
    private val onItemClick: (Item) -> Unit
) : RecyclerView.Adapter<GridAdapter.ViewHolder>() {
    private var filteredItemList = itemList.toMutableList() // Copy of itemList for filtering

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_grid, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = filteredItemList[position] // Use filteredItemList instead of itemList

        // Capitalize the first letter of item name and description
        val capitalizedItemName = item.itemName.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase() else it.toString()
        }
        val capitalizedItemDescription = item.itemDescription.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase() else it.toString()
        }

        holder.textViewItemName.text = capitalizedItemName
        holder.textViewItemDescription.text = capitalizedItemDescription
        holder.textViewItemPrice.text = String.format("₹%.2f", item.itemPrice)

        // Set click listener
        holder.itemView.setOnClickListener {
            onItemClick(item) // Pass clicked item back to Fragment
        }
    }

    override fun getItemCount(): Int = filteredItemList.size // Use filteredItemList instead of itemList

    // Method to update the items in the adapter
    fun updateItems(newItems: List<Item>) {
        itemList = newItems
        filteredItemList = itemList.toMutableList() // Reset filtered list
        notifyDataSetChanged()
    }

    fun filterItems(query: String) {
        if (query.isEmpty()) {
            // If query is empty, reset to the full list
            filteredItemList = itemList.toMutableList()
        } else {
            // Filter based on query
            filteredItemList = itemList.filter { item ->
                item.itemName.contains(query, ignoreCase = true) ||
                        item.itemDescription.contains(query, ignoreCase = true)
            }.toMutableList()

            // Log to debug the filtering process
            Log.d("GridAdapter", "Query: $query, Matching items count: ${filteredItemList.size}")
        }
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewItemName: TextView = itemView.findViewById(R.id.textViewItemName)
        val textViewItemDescription: TextView = itemView.findViewById(R.id.textViewItemDescription)
        val textViewItemPrice: TextView = itemView.findViewById(R.id.textViewItemPrice)
    }
}