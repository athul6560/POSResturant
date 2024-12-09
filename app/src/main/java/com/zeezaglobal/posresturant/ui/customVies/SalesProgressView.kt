package com.zeezaglobal.posresturant.ui.customVies

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.zeezaglobal.posresturant.R
import kotlin.math.roundToInt

class SalesProgressView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val totalSalesText: TextView
    private val cashProgressBar: ProgressBar
    private val upiProgressBar: ProgressBar
    private val creditCardProgressBar: ProgressBar
    private val cashSalesAmount: TextView
    private val upiSalesAmount: TextView
    private val creditCardSalesAmount: TextView
    private val cashPercentageText: TextView
    private val upiPercentageText: TextView
    private val creditCardPercentageText: TextView

    init {
        orientation = VERTICAL
        val view = LayoutInflater.from(context).inflate(R.layout.view_sales_progress, this, true)
        totalSalesText = view.findViewById(R.id.totalSalesText)
        cashProgressBar = view.findViewById(R.id.cashProgressBar)
        upiProgressBar = view.findViewById(R.id.upiProgressBar)
        creditCardProgressBar = view.findViewById(R.id.creditCardProgressBar)
        cashSalesAmount = view.findViewById(R.id.cashSalesAmount)
        upiSalesAmount = view.findViewById(R.id.upiSalesAmount)
        creditCardSalesAmount = view.findViewById(R.id.creditCardSalesAmount)
        cashPercentageText = view.findViewById(R.id.cashPercentageText)
        upiPercentageText = view.findViewById(R.id.upiPercentageText)
        creditCardPercentageText = view.findViewById(R.id.creditCardPercentageText)
    }

    fun setSalesData(cashSales: Double, upiSales: Double, creditCardSales: Double) {
        val totalSales = cashSales + upiSales + creditCardSales
        totalSalesText.text = "Total Sales: ₹$totalSales"

        if (totalSales > 0) {
            val cashPercentage = ((cashSales * 100.0) / totalSales).roundToInt()
            val upiPercentage = ((upiSales * 100.0) / totalSales).roundToInt()
            val creditCardPercentage = ((creditCardSales * 100.0) / totalSales).roundToInt()

// Set progress bars with integer percentage (no decimal points)
            cashProgressBar.progress = cashPercentage.toInt()
            upiProgressBar.progress = upiPercentage.toInt()
            creditCardProgressBar.progress = creditCardPercentage.toInt()

            // Update individual sales amounts
            cashSalesAmount.text = "₹$cashSales"
            upiSalesAmount.text = "₹$upiSales"
            creditCardSalesAmount.text = "₹$creditCardSales"

            // Update percentages
            cashPercentageText.text = "$cashPercentage%"
            upiPercentageText.text = "$upiPercentage%"
            creditCardPercentageText.text = "$creditCardPercentage%"
        } else {
            // Handle zero total sales
            cashProgressBar.progress = 0
            upiProgressBar.progress = 0
            creditCardProgressBar.progress = 0
            cashSalesAmount.text = "₹0"
            upiSalesAmount.text = "$0"
            creditCardSalesAmount.text = "$0"
            cashPercentageText.text = "0%"
            upiPercentageText.text = "0%"
            creditCardPercentageText.text = "0%"
        }
    }
}