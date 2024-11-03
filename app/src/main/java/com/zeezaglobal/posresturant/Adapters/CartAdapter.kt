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
    private val onQuantityChanged: (List<CartItem>) -> Unit,
    private val onItemAdded: (CartItem) -> Unit,
    private val onItemSubtracted: (CartItem) -> Unit
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
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cart_layout, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = itemList[position]
        holder.itemName.text = item.item.itemName.capitalize()
        holder.itemDescription.text = item.item.itemDescription.capitalize()
        holder.itemPrice.text = "₹${item.item.itemPrice}"
        holder.itemQuantity.text = item.quantity.toString()

        holder.addItemButton.setOnClickListener {
            onItemAdded(item) // Trigger add callback
        }

        holder.subtractItemButton.setOnClickListener {
            onItemSubtracted(item) // Trigger subtract callback
        }
    }

    override fun getItemCount(): Int = itemList.size

    fun updateItems(newItems: List<CartItem>) {
      //  itemList.clear()
      //  itemList.addAll(newItems)
        notifyDataSetChanged()
    }
}