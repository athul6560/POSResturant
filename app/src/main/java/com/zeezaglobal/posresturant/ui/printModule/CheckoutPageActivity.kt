package com.zeezaglobal.posresturant.ui.printModule

import Receipt
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import android.Manifest
import android.bluetooth.BluetoothDevice
import android.content.pm.PackageManager
import android.view.View
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
import com.zeezaglobal.posresturant.Utils.StorePreferenceManager
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
    private lateinit var cartItemList: List<CartItem>
    private var token: Int? = null
    private var billNo: Long? = null
    private lateinit var btnCash: LinearLayout
    private lateinit var btnUpi: LinearLayout
    private lateinit var btnCard: LinearLayout
    private var selectedPaymentMethod: String = "Cash"
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

        // This app targets an SDK that enforces edge-to-edge rendering, so content draws
        // behind the status/navigation bars unless we explicitly pad for them here.
        val rootView = findViewById<View>(android.R.id.content)
        ViewCompat.setOnApplyWindowInsetsListener(rootView) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize your views
        saveCheck = findViewById(R.id.save_check)
        finishBtn = findViewById(R.id.finish_btn)
        printBtn = findViewById(R.id.button_bluetooth)
        tokenBtn = findViewById(R.id.tocken_btn)
        subtotalTextView = findViewById(R.id.textView8)
        taxTextView = findViewById(R.id.textView9)
        totalTextView = findViewById(R.id.textView10)
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
        printBtn.visibility = View.GONE
        tokenBtn.visibility = View.GONE

        cartItemList = CartItemStore.cartItemList!!

        // Set up payment buttons
        btnCash = findViewById(R.id.btn_payment_cash)
        btnUpi = findViewById(R.id.btn_payment_upi)
        btnCard = findViewById(R.id.btn_payment_card)

        selectedPaymentMethod = CartItemStore.paymentMethod ?: "Cash"
        updatePaymentSelection(selectedPaymentMethod)

        btnCash.setOnClickListener { selectPayment("Cash") }
        btnUpi.setOnClickListener { selectPayment("UPI") }
        btnCard.setOnClickListener { selectPayment("Card") }
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
                billNumber = billNo ?: 0L,
                tokenNumber = token ?: 0,
                totalAmount = subtotal,
                dateTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date()),
                paymentMethod = selectedPaymentMethod,
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

    private fun selectPayment(method: String) {
        selectedPaymentMethod = method
        CartItemStore.paymentMethod = method
        updatePaymentSelection(method)
    }

    private fun updatePaymentSelection(method: String) {
        setPaymentUnselected(btnCash)
        setPaymentUnselected(btnUpi)
        setPaymentUnselected(btnCard)
        when (method) {
            "Cash" -> setPaymentSelected(btnCash, ContextCompat.getColor(this, R.color.cashColor))
            "UPI"  -> setPaymentSelected(btnUpi,  ContextCompat.getColor(this, R.color.upiColor))
            "Card" -> setPaymentSelected(btnCard, ContextCompat.getColor(this, R.color.creditCardColor))
        }
    }

    private fun setPaymentSelected(btn: LinearLayout, color: Int) {
        val cornerPx = (20 * resources.displayMetrics.density)
        val drawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = cornerPx
            setColor(color)
        }
        btn.background = drawable
        // Make all child TextViews white
        for (i in 0 until btn.childCount) {
            (btn.getChildAt(i) as? TextView)?.setTextColor(
                ContextCompat.getColor(this, R.color.white)
            )
        }
    }

    private fun setPaymentUnselected(btn: LinearLayout) {
        btn.setBackgroundResource(R.drawable.payment_btn_unselected)
        // Restore text colors
        val black = ContextCompat.getColor(this, R.color.black)
        for (i in 0 until btn.childCount) {
            (btn.getChildAt(i) as? TextView)?.setTextColor(black)
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
            customerPhone = customerPhone,
            status = 0,
            store = getstoreId()
        )
        SaleItem = saleItem
        saleRepository.insertSale(saleItem, this)
        printBtn.visibility = View.VISIBLE
        tokenBtn.visibility = View.VISIBLE
    }

    private fun getstoreId(): Int {
        val storeId = StorePreferenceManager.getStoreId(this)
       return storeId
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
