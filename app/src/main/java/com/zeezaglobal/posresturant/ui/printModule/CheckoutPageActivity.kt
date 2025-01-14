package com.zeezaglobal.posresturant.ui.printModule

import Receipt
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import android.Manifest
import android.bluetooth.BluetoothDevice
import android.content.pm.PackageManager
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zeezaglobal.posresturant.Adapters.CartItemAdapter
import com.zeezaglobal.posresturant.Application.POSApp
import com.zeezaglobal.posresturant.Dialogues.SuccessDialogFragment
import com.zeezaglobal.posresturant.Entities.CartItem
import com.zeezaglobal.posresturant.Entities.CartItemStore
import com.zeezaglobal.posresturant.Entities.Item
import com.zeezaglobal.posresturant.Entities.Sale
import com.zeezaglobal.posresturant.Printer.BTPrinterLogic

import com.zeezaglobal.posresturant.R
import com.zeezaglobal.posresturant.Repository.SaleRepository
import com.zeezaglobal.posresturant.Utils.BillandTocken
import java.text.SimpleDateFormat
import java.util.Date

class CheckoutPageActivity : AppCompatActivity() {
    private lateinit var subtotalTextView: TextView
    private lateinit var taxTextView: TextView
    private lateinit var totalTextView: TextView

    private lateinit var tokenNumber: TextView
    private lateinit var billNumber: TextView
    private lateinit var dateAndTime: TextView
    private lateinit var customerName: EditText
    private lateinit var saveCheck: Button
    private lateinit var finishBtn: Button
    private lateinit var tokenBtn: Button
    private lateinit var printBtn: Button
    private lateinit var customerEmail: EditText
    private lateinit var customerPhone: EditText
    private lateinit var cartItemList: List<CartItem>  // You need to initialize this properly
    private var token: Int? = null
    private var billNo: Long? = null
    lateinit var paymentMethodSpinner: Spinner
    private lateinit var saleRepository: SaleRepository
    private val taxRate = 0.0
    private lateinit var printerHelper: BTPrinterLogic
    private val PERMISSION_REQUEST_CODE = 1001
    private var selectedPrinterDevice: BluetoothDevice? = null
    private lateinit var SaleItem: Sale


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_checkout_page)
        supportActionBar?.hide()
        // Initialize your views
        saveCheck = findViewById(R.id.save_check)
        finishBtn = findViewById(R.id.finish_btn)
        printBtn = findViewById(R.id.button_bluetooth)
        tokenBtn = findViewById(R.id.tocken_btn)
        subtotalTextView = findViewById(R.id.textView8)
        taxTextView = findViewById(R.id.textView9)
        totalTextView = findViewById(R.id.textView10)
        paymentMethodSpinner = findViewById(R.id.payment_method_spinner)
        tokenNumber = findViewById(R.id.tocken_number)
        billNumber = findViewById(R.id.textView23)
        dateAndTime = findViewById(R.id.textView24)
        customerName = findViewById(R.id.customer_name)
        customerEmail = findViewById(R.id.customer_email)
        customerPhone = findViewById(R.id.customer_phone)
        printerHelper = BTPrinterLogic(this)
        // Initialize RecyclerView
        val recyclerView: RecyclerView = findViewById(R.id.recyclerview_chckout)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize SaleRepository
        saleRepository = SaleRepository((application as POSApp).database.saleDao())
        printBtn.setEnabled(false);
        tokenBtn.setEnabled(false);
        // Assume cartItemList is populated with data from the cart
        cartItemList = CartItemStore.cartItemList!! // Replace with actual cart item list source

        // Set the payment method text
        val paymentMethods = resources.getStringArray(R.array.payment_methods)

// Set up the spinner adapter
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, paymentMethods)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        paymentMethodSpinner.adapter = adapter

// Set the initial selection based on CartItemStore.paymentMethod
        val initialPosition = paymentMethods.indexOf(CartItemStore.paymentMethod)
        if (initialPosition != -1) {
            paymentMethodSpinner.setSelection(initialPosition)
        }
        paymentMethodSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                val selectedMethod = parent.getItemAtPosition(position).toString()
                CartItemStore.paymentMethod=selectedMethod
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Handle case when nothing is selected, if needed
            }
        }
        // Calculate and display totals
        calculateTotals()

        // Generate token and bill number
        val generator = BillandTocken(this)
        token = generator.generateToken() // Assume this method returns a token number
        billNo = generator.generateUniqueBillNumber() // Assume this returns a unique bill number

        tokenNumber.text = "Token : $token"
        billNumber.text = "Bill Number : $billNo"
        dateAndTime.text = "Date & Time : ${getCurrentDateAndTime()}"
        tokenBtn.setOnClickListener {
            if (printerHelper.checkBluetoothPermissions()) {
                if (selectedPrinterDevice != null) {
                    printToken()
                } else {
                    printerHelper.selectPrinter { printerDevice ->
                        try {
                            selectedPrinterDevice = printerDevice
                            printerHelper.connectToPrinter(printerDevice)

                            printToken()
                        } catch (e: Exception) {
                            e.printStackTrace()
                            Toast.makeText(this, "Failed to print: ${e.message}", Toast.LENGTH_LONG)
                                .show()
                        }
                    }
                }
            } else {
                requestBluetoothPermissions()
            }
        }
        // Set listeners
        finishBtn.setOnClickListener {
            if (selectedPrinterDevice != null) {
                try {
                    printerHelper.disconnectPrinter()
                    selectedPrinterDevice = null
                    //   Toast.makeText(this, "Printer disconnected successfully.", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    e.printStackTrace()
                    //   Toast.makeText(this, "Failed to disconnect printer: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
            finish()
        }
        if (!checkBluetoothPermissions()) {
            requestBluetoothPermissions()
        }
        printBtn.setOnClickListener {
            if (printerHelper.checkBluetoothPermissions()) {
                if (selectedPrinterDevice != null) {
                    printreceipt()
                } else {
                    printerHelper.selectPrinter { printerDevice ->
                        try {
                            selectedPrinterDevice = printerDevice
                            printerHelper.connectToPrinter(printerDevice)

                            printreceipt()
                        } catch (e: Exception) {
                            e.printStackTrace()
                            Toast.makeText(this, "Failed to print: ${e.message}", Toast.LENGTH_LONG)
                                .show()
                        }
                    }
                }
            } else {
                requestBluetoothPermissions()
            }
        }



        saveCheck.setOnClickListener {
            // Check if cartItemList is not empty before calculating subtotal
            val subtotal = if (cartItemList.isNotEmpty()) {
                cartItemList.sumOf { cartItem -> cartItem.item.itemPrice * cartItem.quantity }
            } else {
                0.0
            }

            // Call addToSales only if values are valid
            addToSales(
                billNumber = billNo ?: 0L, // Default to 0 if null
                tokenNumber = token ?: 0,  // Default to 0 if null
                totalAmount = subtotal,
                dateTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date()),
                paymentMethod = CartItemStore.paymentMethod.toString(),
                customerName = customerName.text.toString(),
                customerEmail = customerEmail.text.toString(),
                customerPhone = customerPhone.text.toString()
            )
        }

        if (cartItemList.isNotEmpty()) {
            val adapter = CartItemAdapter(cartItemList)
            recyclerView.adapter = adapter
        }
    }

    private fun printToken() {
        printerHelper.printToken(SaleItem)
    }

    private fun addToSales(
        billNumber: Long,
        tokenNumber: Int,
        totalAmount: Double,
        dateTime: String,
        paymentMethod: String,
        customerName: String,
        customerEmail: String,
        customerPhone: String
    ) {


        val saleItem = Sale(
            billNumber = billNumber,
            tokenNumber = tokenNumber,
            totalAmount = totalAmount,
            dateTime = dateTime,
            paymentMethod = paymentMethod,
            items = cartItemList, // Assign the passed list of CartItems
            customerName = customerName,
            customerEmail = customerEmail,
            customerPhone = customerPhone
        )
        SaleItem = saleItem
        saleRepository.insertSale(saleItem)
        printBtn.setEnabled(true);
        tokenBtn.setEnabled(true);
    }

    private fun calculateTotals() {
        var subtotal = 0.0
        for (cartItem in cartItemList) {
            subtotal += cartItem.item.itemPrice * cartItem.quantity
        }

        val tax = subtotal * taxRate
        val total = subtotal + tax

        // Display calculated values in the TextViews
        subtotalTextView.text = String.format("₹%.2f", subtotal)
        taxTextView.text = String.format("₹%.2f", tax)
        totalTextView.text = String.format("₹%.2f", total)
    }


    private fun checkBluetoothPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.BLUETOOTH_CONNECT
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestBluetoothPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
            PERMISSION_REQUEST_CODE
        )
    }

    private fun getCurrentDateAndTime(): String {
        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
    }

    private fun printreceipt() {

        printerHelper.printReceipt(SaleItem)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Bluetooth permissions granted.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(
                    this,
                    "Bluetooth permissions are required to connect to the printer.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onBackPressed() {
        // Do nothing, preventing back navigation
        Toast.makeText(this, "Press Finish button to go back", Toast.LENGTH_SHORT).show()
    }
}
