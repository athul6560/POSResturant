package com.zeezaglobal.posresturant.Printer

import Receipt
import Token
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import com.zeezaglobal.posresturant.Entities.CartItem
import com.zeezaglobal.posresturant.Entities.Sale
import java.io.OutputStream
import java.util.UUID

class BTPrinterLogic(private val context: Context) {

    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private var bluetoothSocket: BluetoothSocket? = null
    private var outputStream: OutputStream? = null
    private val printerUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB") // Standard SPP UUID
    private var selectedPrinterDevice: BluetoothDevice? = null
    private val PERMISSION_REQUEST_CODE = 1001

    fun checkBluetoothPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.BLUETOOTH_CONNECT
        ) == PackageManager.PERMISSION_GRANTED
    }
    fun disconnectPrinter() {
        try {
            if (bluetoothSocket != null && bluetoothSocket!!.isConnected) {
                bluetoothSocket!!.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw Exception("Error disconnecting printer: ${e.message}")
        }
    }
    fun selectPrinter(onPrinterSelected: (BluetoothDevice) -> Unit) {
        // Check if the BLUETOOTH_CONNECT permission is granted
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // Request the permission from the user or show a toast
            ActivityCompat.requestPermissions(
                (context as AppCompatActivity), // Ensure context is an Activity to request permissions
                arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
                PERMISSION_REQUEST_CODE
            )
            Toast.makeText(context, "Bluetooth permissions are not granted. Please allow permissions and try again.", Toast.LENGTH_LONG).show()
            return
        }

        // Fetch paired Bluetooth devices
        val pairedDevices = bluetoothAdapter?.bondedDevices
        if (pairedDevices.isNullOrEmpty()) {
            Toast.makeText(context, "No paired Bluetooth devices found.", Toast.LENGTH_LONG).show()
            return
        }

        // Display paired devices in a dialog
        val deviceNames = pairedDevices.map { it.name }
        val deviceList = pairedDevices.toList()

        val builder = AlertDialog.Builder(context)
        builder.setTitle("Select a Printer")
        builder.setItems(deviceNames.toTypedArray()) { _, which ->
            val selectedDevice = deviceList[which]
            onPrinterSelected(selectedDevice)
        }
        builder.setCancelable(false)
        builder.show()
    }

    fun connectToPrinter(printerDevice: BluetoothDevice) {
        // Check if the BLUETOOTH_CONNECT permission is granted
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // Notify the user about missing permissions
            Toast.makeText(context, "Bluetooth permissions are not granted.", Toast.LENGTH_LONG).show()
            return
        }

        try {
            // If already connected, don't reconnect
            if (bluetoothSocket == null || !bluetoothSocket!!.isConnected) {
                bluetoothSocket = printerDevice.createRfcommSocketToServiceRecord(printerUUID)
                bluetoothSocket?.connect()
                outputStream = bluetoothSocket?.outputStream
            }
            selectedPrinterDevice = printerDevice
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Failed to connect to printer: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    fun printReceipt(saleItem: Sale) {
        if (outputStream == null) throw Exception("Output stream is null")

        try {
            val receiptGenerator = Receipt(saleItem)
            val receiptStream = receiptGenerator.generateReceiptStream()

            // Write receipt content to the printer
            outputStream?.write(receiptStream.toByteArray())
            outputStream?.flush()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Failed to print receipt: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
    fun printToken(saleItem: Sale) {
        if (outputStream == null) throw Exception("Output stream is null")

        try {
            val receiptGenerator = Token(saleItem)
            val receiptStream = receiptGenerator.generateTokenStream()

            // Write receipt content to the printer
            outputStream?.write(receiptStream.toByteArray())
            outputStream?.flush()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Failed to print receipt: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    fun closeConnection() {
        try {
            outputStream?.close()
            bluetoothSocket?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            bluetoothSocket = null
            outputStream = null
        }
    }
}

