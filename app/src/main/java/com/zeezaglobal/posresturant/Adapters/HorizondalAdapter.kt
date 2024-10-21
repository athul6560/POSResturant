package com.zeezaglobal.posresturant.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zeezaglobal.posresturant.Entities.Item
import com.zeezaglobal.posresturant.R

class HorizondalAdapter(
    private var itemList: List<String>,
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<HorizondalAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemText: TextView = view.findViewById(R.id.group_tv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.group_view, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        // Capitalize the first letter of each item
        val currentItem = itemList[position].replaceFirstChar {
            if (it.isLowerCase()) it.titlecase() else it.toString()
        }
        holder.itemText.text = currentItem

        // Set a click listener for the item
        holder.itemView.setOnClickListener {
            onItemClick(currentItem)
        }
    }
    fun updateItems(newItems: List<String>) {
        itemList = newItems
        notifyDataSetChanged() // Notify the adapter of data changes
    }
    override fun getItemCount(): Int = itemList.size
}