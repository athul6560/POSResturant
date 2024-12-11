package com.zeezaglobal.posresturant.ui.slideshow

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.zeezaglobal.posresturant.Adapters.SalesAdapter
import com.zeezaglobal.posresturant.Application.POSApp
import com.zeezaglobal.posresturant.Entities.Sale
import com.zeezaglobal.posresturant.R
import com.zeezaglobal.posresturant.Repository.SaleRepository
import com.zeezaglobal.posresturant.Utils.DateTimeUtils
import com.zeezaglobal.posresturant.databinding.FragmentAnalyticsBinding
import com.zeezaglobal.posresturant.ui.customVies.SalesProgressView
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class AnalyticsFragment : Fragment() {

    private var _binding: FragmentAnalyticsBinding? = null
    private lateinit var analyticsViewModel: AnalyticsViewModel

    private val binding get() = _binding!!
    private lateinit var textView22: TextView
    private lateinit var subHeadingDate: TextView
    private lateinit var textView28: TextView
    private lateinit var textView29: TextView
    private lateinit var totalSalesText: TextView
    private lateinit var textView31: TextView
    private lateinit var heading: TextView
    private lateinit var calender_btn: ConstraintLayout
    private lateinit var subheading: TextView
    private lateinit var dateSelectionDate: TextView
    private lateinit var saleAmount: TextView
    private lateinit var ExportButton: Button
    private lateinit var _saleList: List<Sale>
    private lateinit var salesView: SalesProgressView
    private lateinit var unit: TextView
    private  val RANGE_SELECTION=0
    private  val DAY_SELECTION=1


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val application = requireActivity().application as POSApp
        val saleRepository = SaleRepository((application).database.saleDao())
        val saleViewModelFactory = SaleViewModelFactory(saleRepository)
        analyticsViewModel = ViewModelProvider(this, saleViewModelFactory).get(
            AnalyticsViewModel::class.java
        )
        _binding = FragmentAnalyticsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        textView22 = root.findViewById(R.id.textView22)
        subHeadingDate = root.findViewById(R.id.textView26)
        dateSelectionDate = root.findViewById(R.id.DateSelectionText)
        textView28 = root.findViewById(R.id.textView28)
        calender_btn = root.findViewById(R.id.calender_btn)
        textView29 = root.findViewById(R.id.textView29)
        totalSalesText = root.findViewById(R.id.textView30)
        textView31 = root.findViewById(R.id.textView31)
        heading = root.findViewById(R.id.heading)
        subheading = root.findViewById(R.id.subheading)
        saleAmount = root.findViewById(R.id.sale_amount)
        ExportButton = root.findViewById(R.id.export_button2)
        unit = root.findViewById(R.id.unit)
        salesView = root.findViewById(R.id.salesProgressView)
        val recyclerView: RecyclerView = root.findViewById(R.id.salesRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())


        val adapter = SalesAdapter(emptyList())
        recyclerView.adapter = adapter
        // Get today's date
        subHeadingDate.text = DateTimeUtils.getCurrentDate()
        dateSelectionDate.text = DateTimeUtils.getCurrentDate()
        analyticsViewModel.sales.observe(viewLifecycleOwner, Observer { saleList ->
            _saleList = saleList
            adapter.updateSales(saleList)
            setGraph(saleList)
            totalSalesText.setText(calculateTotalSales(saleList).toString())
            saleAmount.setText("₹" + calculateTotalSalesAmount(saleList))
        })
        val currentDate = Date()
        analyticsViewModel.fetchSalesForDate(currentDate)
        ExportButton.setOnClickListener {
            exportSalesToCSV(_saleList)
        }
        calender_btn.setOnClickListener {
            //  calenderPopup()
            showDropdownMenu(this.calender_btn)
        }
        return root
    }

    private fun showDropdownMenu(anchor: View) {
        val popupMenu = PopupMenu(requireContext(), anchor)
        popupMenu.menuInflater.inflate(R.menu.dropdown_menu, popupMenu.menu)

        // Handle menu item clicks
        popupMenu.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.menu_option1 -> {
                    calenderPopup(DAY_SELECTION)
                    true
                }

                R.id.menu_option2 -> {
                    calenderPopup(RANGE_SELECTION)
                    true
                }

                else -> false
            }
        }

        popupMenu.show()
    }

    private fun calenderPopup(mode: Int) {
        val calendarConstraints = CalendarConstraints.Builder()
            .setStart(System.currentTimeMillis()) // Optional: Set start date
            .setEnd(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 365) // Optional: Set end date (1 year ahead)
            .build()

        // To select a single date
        val singleDatePicker = MaterialDatePicker.Builder.datePicker()
            .setCalendarConstraints(calendarConstraints)
            .build()

        singleDatePicker.addOnPositiveButtonClickListener { selection ->
            val selectedDate =
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(selection))
            // Use selectedDate as needed
            Toast.makeText(requireContext(), "Selected Date: $selectedDate", Toast.LENGTH_SHORT)
                .show()
        }

        // To select a date range
        val dateRangePicker = MaterialDatePicker.Builder.dateRangePicker()
            .setCalendarConstraints(calendarConstraints)
            .build()

        dateRangePicker.addOnPositiveButtonClickListener { selection ->
            val startDate =
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(selection.first))
            val endDate =
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(selection.second))
            // Use startDate and endDate as needed
            Toast.makeText(
                requireContext(),
                "Selected Range: $startDate to $endDate",
                Toast.LENGTH_SHORT
            ).show()
        }
        if (mode == 1)
        // Show the date picker dialog
            singleDatePicker.show(requireActivity().supportFragmentManager, "DATE_PICKER")
        // Alternatively, for range selection
        else
            dateRangePicker.show(requireActivity().supportFragmentManager, "DATE_RANGE_PICKER")
    }


    private fun exportSalesToCSV(saleList: List<Sale>?) {
        if (saleList.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "No sales data to export", Toast.LENGTH_SHORT).show()
            return
        }
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val currentDate = dateFormat.format(Date())
        // Save file in internal storage
        val directory = File(requireContext().filesDir, "exports")
        if (!directory.exists()) directory.mkdirs()

        val csvFile = File(directory, "SalesData_$currentDate.csv")
        try {
            FileWriter(csvFile).use { writer ->
                // CSV Header
                writer.append("ID,Bill Number,Token Number,Total Amount,Date,Payment Method,Items\n")

                saleList.forEach { sale ->
                    // Serialize items into a single string
                    val itemsString = sale.items.joinToString(separator = ";") { item ->
                        "(${item.item},${item.quantity})"
                    }

                    // Write sale data
                    writer.append(
                        "${sale.id}," +
                                "${sale.billNumber}," +
                                "${sale.tokenNumber}," +
                                "${sale.totalAmount}," +
                                "${sale.dateTime}," +
                                "${sale.paymentMethod}," +
                                "\"$itemsString\"\n" // Quote the items string to handle any special characters
                    )
                }
                writer.flush()
            }

            // Share the file using FileProvider
            shareCSVFile(csvFile)


        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Failed to export sales", Toast.LENGTH_SHORT).show()
        }
    }

    private fun shareCSVFile(file: File) {
        val fileUri = androidx.core.content.FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.fileprovider",
            file
        )

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_STREAM, fileUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        startActivity(Intent.createChooser(shareIntent, "Share Sales Data"))
    }


    private fun calculateTotalSalesAmount(saleList: List<Sale>?): Double {
        // Check if saleList is null or empty, return 0.0 if true
        if (saleList.isNullOrEmpty()) {
            return 0.0
        }

        // Sum the totalAmount of all Sales in the list
        val totalSales = saleList.sumOf { it.totalAmount }

        return totalSales
    }

    private fun calculateTotalSales(saleList: List<Sale>?): Int {
        // Check if saleList is null or empty, return 0 if true
        return saleList?.size ?: 0
    }


    private fun setGraph(saleList: List<Sale>?) {
        var cashSales = 0.0
        var upiSales = 0.0
        var creditCardSales = 0.0

        saleList?.forEach { sale ->
            when (sale.paymentMethod) {
                "Cash" -> cashSales += sale.totalAmount // Assuming 'amount' is the sale amount
                "UPI" -> upiSales += sale.totalAmount
                "Debit/Credit Card" -> creditCardSales += sale.totalAmount
                else -> {} // Handle any other payment types if necessary
            }
        }

        // Set the sales data using the calculated values
        salesView.setSalesData(
            cashSales = cashSales,
            upiSales = upiSales,
            creditCardSales = creditCardSales
        )
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}