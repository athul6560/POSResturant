package com.zeezaglobal.posresturant.Dialogues

import android.app.AlertDialog
import android.content.Context

class PaymentMethodDialog(private val context: Context) {

    interface PaymentMethodListener {
        fun onPaymentMethodSelected(method: String)
    }

    private var listener: PaymentMethodListener? = null

    fun setPaymentMethodListener(listener: PaymentMethodListener) {
        this.listener = listener
    }

    fun show() {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Select Payment Method")

        builder.setPositiveButton("Cash") { dialog, _ ->
            listener?.onPaymentMethodSelected("Cash")
            dialog.dismiss()
        }

        builder.setNeutralButton("UPI") { dialog, _ ->
            listener?.onPaymentMethodSelected("UPI")
            dialog.dismiss()
        }

        builder.setNegativeButton("Debit/Credit Card") { dialog, _ ->
            listener?.onPaymentMethodSelected("Debit/Credit Card")
            dialog.dismiss()
        }

        val alertDialog = builder.create()
        alertDialog.show()
    }
}