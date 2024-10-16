package com.zeezaglobal.posresturant.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zeezaglobal.posresturant.Entities.CartItem
import com.zeezaglobal.posresturant.Entities.Item
import com.zeezaglobal.posresturant.R
import com.zeezaglobal.posresturant.Utils.SharedPreferencesHelper

class CartAdapter(
    private var itemList: MutableList<CartItem>,
    private val sharedPreferencesHelper: SharedPreferencesHelper,
    private val onQuantityChanged: (List<CartItem>) -> Unit
) : RecyclerView.Adapter<CartAdapter.ItemViewHolder>() {

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemName: TextView = itemView.findViewById(R.id.item_name_text_view)
        val itemDescription: TextView = itemView.findViewById(R.id.item_description_text_view)
        val itemPrice: TextView = itemView.findViewById(R.id.item_price_text_view)
        val itemQuantity: TextView = itemView.findViewById(R.id.item_quantity)
        val addItemButton: RelativeLayout = itemView.findViewById(R.id.add_item_relative_layout)
        val subtractItemButton: RelativeLayout = itemView.findViewById(R.id.subtract_item_relative_layout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cart_layout, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = itemList[position]
        // Capitalize the first letter of item name and description
        val capitalizedItemName = item.item.itemName.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase() else it.toString()
        }
        val capitalizedItemDescription = item.item.itemDescription.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase() else it.toString()
        }

        holder.itemName.text = capitalizedItemName
        holder.itemDescription.text = capitalizedItemDescription
        holder.itemPrice.text = "₹${item.item.itemPrice}" // Format price as needed
        holder.itemQuantity.text = item.quantity.toString()

        // Set click listener for add button
        holder.addItemButton.setOnClickListener {
            item.quantity++ // Increase quantity
            sharedPreferencesHelper.saveCartItemToSharedPreferences(item.item) // Update SharedPreferences
            notifyItemChanged(position) // Notify the adapter that item has changed
            onQuantityChanged(itemList) // Notify about quantity change
        }

        // Set click listener for subtract button
        holder.subtractItemButton.setOnClickListener {
            if (item.quantity > 0) {
                item.quantity-- // Decrease quantity
                if (item.quantity == 0) {
                    removeItem(position) // Remove item if quantity is zero
                } else {
                    notifyItemChanged(position) // Notify the adapter that item has changed
                }
                sharedPreferencesHelper.saveCartItemToSharedPreferences(item.item) // Update SharedPreferences
                onQuantityChanged(itemList) // Notify about quantity change
            }
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    // Function to remove an item
    private fun removeItem(position: Int) {
        itemList.removeAt(position) // Remove item from the list
        notifyItemRemoved(position) // Notify that the item was removed
        notifyItemRangeChanged(position, itemList.size) // Notify about the item range change
        // Also remove from SharedPreferences if item is removed
        sharedPreferencesHelper.clearCart()
    }

    fun updateItems(newItems: List<CartItem>) {
        itemList.clear() // Clear the existing items
        itemList.addAll(newItems) // Add new items
        notifyDataSetChanged()
    }
}