package com.zeezaglobal.posresturant.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.zeezaglobal.posresturant.Entities.Sale
import com.zeezaglobal.posresturant.R
import java.text.SimpleDateFormat
import java.util.Locale

class SalesAdapter(private var salesList: List<Sale>) : RecyclerView.Adapter<SalesAdapter.SaleViewHolder>() {
    private val inputDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    private val outputDateFormat = SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault())
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SaleViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_sale, parent, false)
        return SaleViewHolder(view)
    }

    override fun onBindViewHolder(holder: SaleViewHolder, position: Int) {
        val sale = salesList[position]

        holder.saleId.text = "Sale ID: ${sale.id}"
        holder.billNumber.text = "Bill Number: ${sale.billNumber}, ${sale.customerName}, ${sale.customerEmail}, ${sale.customerPhone}"
        holder.tokenNumber.text = "Token: ${sale.tokenNumber}"
        holder.totalAmount.text = "Total: ₹${sale.totalAmount}"
        holder.dateTime.text = "Date/Time: ${formatDate(sale.dateTime)}"

        // Convert items to a readable string if necessary
     //   holder.items.text = "Items: ${Gson().toJson(sale.items)}"

    }
    private fun formatDate(dateTime: String): String {
        return try {
            val parsedDate = inputDateFormat.parse(dateTime)
            if (parsedDate != null) {
                outputDateFormat.format(parsedDate)
            } else {
                dateTime // Return original if parsing fails
            }
        } catch (e: Exception) {
            dateTime // Return original if an error occurs
        }
    }
    override fun getItemCount(): Int {
        return salesList.size
    }
    fun updateSales(newSalesList: List<Sale>) {
        salesList = ArrayList(newSalesList) // Assign a new list
        notifyDataSetChanged()
    }
    class SaleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val saleId: TextView = itemView.findViewById(R.id.saleId)
        val billNumber: TextView = itemView.findViewById(R.id.billNumber)
        val tokenNumber: TextView = itemView.findViewById(R.id.tokenNumber)
        val totalAmount: TextView = itemView.findViewById(R.id.totalAmount)
        val dateTime: TextView = itemView.findViewById(R.id.dateTime)

    }
}