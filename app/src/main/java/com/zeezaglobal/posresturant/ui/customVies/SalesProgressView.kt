package com.zeezaglobal.posresturant.ui.customVies

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.zeezaglobal.posresturant.R

class SalesProgressView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val totalSalesText: TextView
    private val cashProgressBar: ProgressBar
    private val upiProgressBar: ProgressBar
    private val creditCardProgressBar: ProgressBar
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
        cashPercentageText = view.findViewById(R.id.cashPercentageText)
        upiPercentageText = view.findViewById(R.id.upiPercentageText)
        creditCardPercentageText = view.findViewById(R.id.creditCardPercentageText)
    }

    fun setSalesData(cashSales: Int, upiSales: Int, creditCardSales: Int) {
        val totalSales = cashSales + upiSales + creditCardSales
        totalSalesText.text = "Total Sales: $totalSales"

        if (totalSales > 0) {
            val cashPercentage = (cashSales * 100) / totalSales
            val upiPercentage = (upiSales * 100) / totalSales
            val creditCardPercentage = (creditCardSales * 100) / totalSales

            cashProgressBar.progress = cashPercentage
            upiProgressBar.progress = upiPercentage
            creditCardProgressBar.progress = creditCardPercentage

            cashPercentageText.text = "$cashPercentage%"
            upiPercentageText.text = "$upiPercentage%"
            creditCardPercentageText.text = "$creditCardPercentage%"
        }
    }
}