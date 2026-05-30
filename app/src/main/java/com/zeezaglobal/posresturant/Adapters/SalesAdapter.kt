package com.zeezaglobal.posresturant.Adapters

import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.zeezaglobal.posresturant.Entities.Sale
import com.zeezaglobal.posresturant.R
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.Locale

class SalesAdapter(
    private var salesList: List<Sale>,
    private val listener: OnPrintClickListener,
    private val cancelListener: OnCancelClickListener
) : RecyclerView.Adapter<SalesAdapter.SaleViewHolder>() {

    interface OnPrintClickListener {
        fun onPrintClick(sale: Sale)
    }

    interface OnCancelClickListener {
        fun onCancelClick(sale: Sale)
    }

    private val inputDateFormat  = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    private val outputDateFormat = SimpleDateFormat("MMM dd, yyyy · hh:mm a", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SaleViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_sale, parent, false)
        return SaleViewHolder(view)
    }

    override fun onBindViewHolder(holder: SaleViewHolder, position: Int) {
        val sale = salesList[position]
        val ctx  = holder.itemView.context

        // Token badge
        holder.tokenNumber.text = sale.tokenNumber.toString()

        // Bill number
        holder.billNumber.text = "#${sale.billNumber}"

        // Customer name + phone
        val name  = sale.customerName?.takeIf { it.isNotBlank() } ?: "Guest"
        val phone = sale.customerPhone?.takeIf { it.isNotBlank() }
        holder.saleId.text = if (phone != null) "$name · $phone" else name

        // Date / time
        holder.dateTime.text = formatDate(sale.dateTime)

        // Total amount
        holder.totalAmount.text = "₹${sale.totalAmount.toInt()}"

        // Payment method chip – colour coded
        holder.paymentChip.text = sale.paymentMethod ?: "—"
        val chipColor = when (sale.paymentMethod) {
            "Cash" -> ContextCompat.getColor(ctx, R.color.cashColor)
            "UPI"  -> ContextCompat.getColor(ctx, R.color.upiColor)
            "Card" -> ContextCompat.getColor(ctx, R.color.creditCardColor)
            else   -> ContextCompat.getColor(ctx, R.color.dark_grey)
        }
        holder.paymentChip.backgroundTintList = ColorStateList.valueOf(chipColor)

        // Cancelled badge + card dimming
        if (sale.status == 1) {
            holder.cancelledBadge.visibility = View.VISIBLE
            holder.itemView.alpha = 0.55f
        } else {
            holder.cancelledBadge.visibility = View.GONE
            holder.itemView.alpha = 1f
        }

        // Buttons
        holder.printBtn.setOnClickListener { listener.onPrintClick(sale) }
        holder.cancelBtn.setOnClickListener { cancelListener.onCancelClick(sale) }
        holder.whatsappBtn.setOnClickListener {
            val rawPhone = sale.customerPhone?.trim()?.replace("\\s".toRegex(), "")
            if (rawPhone.isNullOrBlank()) {
                Toast.makeText(ctx, "No phone number on record", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val phoneNumber = "91$rawPhone"
            val message = "Hi, it's BEAN BARREL! How was your drink today? " +
                    "Your opinion helps us brew better every time.\n" +
                    "Please share your thoughts — even a few words mean a lot!\n" +
                    "…IF BUSY PLEASE RATE OUR DRINK FROM 1 to 5 ☕"
            val uri = Uri.parse("https://wa.me/$phoneNumber?text=${URLEncoder.encode(message, "UTF-8")}")
            try {
                ctx.startActivity(Intent(Intent.ACTION_VIEW, uri))
            } catch (e: Exception) {
                Toast.makeText(ctx, "WhatsApp not installed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun formatDate(dateTime: String): String {
        return try {
            val parsed = inputDateFormat.parse(dateTime)
            if (parsed != null) outputDateFormat.format(parsed) else dateTime
        } catch (e: Exception) {
            dateTime
        }
    }

    override fun getItemCount() = salesList.size

    fun updateSales(newList: List<Sale>) {
        salesList = ArrayList(newList)
        notifyDataSetChanged()
    }

    class SaleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tokenNumber: TextView   = itemView.findViewById(R.id.tokenNumber)
        val billNumber: TextView    = itemView.findViewById(R.id.billNumber)
        val saleId: TextView        = itemView.findViewById(R.id.saleId)
        val dateTime: TextView      = itemView.findViewById(R.id.dateTime)
        val totalAmount: TextView   = itemView.findViewById(R.id.totalAmount)
        val paymentChip: TextView   = itemView.findViewById(R.id.paymentChip)
        val cancelledBadge: TextView = itemView.findViewById(R.id.cancelledBadge)
        val printBtn: Button    = itemView.findViewById(R.id.print_btn_sales)
        val cancelBtn: Button   = itemView.findViewById(R.id.cancel_btn_sales)
        val whatsappBtn: Button = itemView.findViewById(R.id.whatsapp_btn)
    }
}
