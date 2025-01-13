package com.zeezaglobal.posresturant.ui.printModule;


import static java.security.AccessController.getContext;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dantsu.escposprinter.connection.DeviceConnection;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections;
import com.dantsu.escposprinter.connection.tcp.TcpConnection;
import com.dantsu.escposprinter.connection.usb.UsbConnection;
import com.dantsu.escposprinter.connection.usb.UsbPrintersConnections;
import com.dantsu.escposprinter.textparser.PrinterTextParserImg;


import com.zeezaglobal.posresturant.Adapters.CartItemAdapter;
import com.zeezaglobal.posresturant.Application.POSApp;
import com.zeezaglobal.posresturant.Dialogues.SuccessDialogFragment;

import com.zeezaglobal.posresturant.Entities.CartItem;
import com.zeezaglobal.posresturant.Entities.Sale;
import com.zeezaglobal.posresturant.Printer.async.AsyncBluetoothEscPosPrint;
import com.zeezaglobal.posresturant.Printer.async.AsyncEscPosPrint;
import com.zeezaglobal.posresturant.Printer.async.AsyncEscPosPrinter;
import com.zeezaglobal.posresturant.Printer.async.AsyncTcpEscPosPrint;
import com.zeezaglobal.posresturant.Printer.async.AsyncUsbEscPosPrint;
import com.zeezaglobal.posresturant.R;

import com.zeezaglobal.posresturant.Entities.CartItemStore;
import com.zeezaglobal.posresturant.Repository.SaleRepository;
import com.zeezaglobal.posresturant.Utils.BillandTocken;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PrintActivity extends AppCompatActivity {
    private TextView subtotalTextView;
    private TextView taxTextView;
    private TextView totalTextView;
    private TextView paymentMethod;
    private TextView TockenNumber;
    private TextView BillNumber;
    private TextView DateandTime;
    private EditText customerName;
    private Button SaveCheck;
    private Button button;
    private EditText customerEmail;
    private EditText customerPhone;
    private List<CartItem> cartItemList;
    private Integer tokenNumber;
    private Long billNumber;
    SaleRepository saleRepository;
    private final double taxRate = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print);
         button = (Button) this.findViewById(R.id.button_bluetooth_browse);
        button.setOnClickListener(view -> browseBluetoothDevice());
        button = (Button) findViewById(R.id.button_bluetooth);
        button.setEnabled(false);
        button.setOnClickListener(view -> printBluetooth());
       // button = (Button) this.findViewById(R.id.button_usb);
        Button finishBtn = (Button) this.findViewById(R.id.finish_btn);
       // button.setOnClickListener(view -> printUsb());
       // button = (Button) this.findViewById(R.id.button_tcp);
        TockenNumber = (TextView) this.findViewById(R.id.tocken_number);
        SaveCheck = (Button) this.findViewById(R.id.save_check);
        paymentMethod = (TextView) this.findViewById(R.id.payment_method);
        BillNumber = (TextView) this.findViewById(R.id.textView23);
        DateandTime = (TextView) this.findViewById(R.id.textView24);

        customerName = (EditText) this.findViewById(R.id.customer_name);
        customerEmail = (EditText) this.findViewById(R.id.customer_email);
        customerPhone = (EditText) this.findViewById(R.id.customer_phone);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        SaveCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double subtotal = cartItemList.stream()
                        .mapToDouble(cartItem -> cartItem.getItem().getItemPrice() * cartItem.getQuantity())
                        .sum();
                addToSales(billNumber,
                        tokenNumber,
                        subtotal,
                        format.format(new Date()),
                        CartItemStore.INSTANCE.getPaymentMethod(),
                        customerName.getText().toString(),
                        customerEmail.getText().toString(),
                        customerPhone.getText().toString()
                );
            }
        });
      //  button.setOnClickListener(view -> printTcp());
        // Access the cartItemList from CartItemStore
        cartItemList = CartItemStore.INSTANCE.getCartItemList();
        // Retrieve the cart item list
        // Initialize TextViews by their IDs
        subtotalTextView = findViewById(R.id.textView8);
        taxTextView = findViewById(R.id.textView9);
        totalTextView = findViewById(R.id.textView10);
        paymentMethod.setText(CartItemStore.INSTANCE.getPaymentMethod());
        // Assume cartItemList is populated with data from the cart
        calculateTotals();
        saleRepository = new SaleRepository(((POSApp) getApplication()).getDatabase().saleDao());

        BillandTocken generator = new BillandTocken(this);
        tokenNumber = generator.generateToken();
        billNumber = generator.generateUniqueBillNumber();
        TockenNumber.setText("Token : " + tokenNumber);
        BillNumber.setText("Bill Number : " + billNumber);

        DateandTime.setText("Date & Time : " + getCurrentDateAndTime());
        // Initialize RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerview_chckout);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        if (cartItemList != null) {
            for (CartItem item : cartItemList) {

                CartItemAdapter adapter = new CartItemAdapter(cartItemList);
                recyclerView.setAdapter(adapter);
            }
        }
    }

    private String getCurrentDateAndTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mma", Locale.getDefault());
        Date currentDate = new Date();
        return dateFormat.format(currentDate).toLowerCase(Locale.getDefault());
    }

    private void calculateTotals() {
        // Calculate subtotal
        double subtotal = 0.0;
        for (CartItem cartItem : cartItemList) {
            subtotal += cartItem.getItem().getItemPrice() * cartItem.getQuantity();
        }

        // Calculate tax
        double tax = subtotal * taxRate;

        // Calculate total
        double total = subtotal + tax;

        // Display calculated values in the TextViews
        subtotalTextView.setText(String.format("₹%.2f", subtotal));
        taxTextView.setText(String.format("₹%.2f", tax));
        totalTextView.setText(String.format("₹%.2f", total));
    }


    /*==============================================================================================
    ======================================BLUETOOTH PART============================================
    ==============================================================================================*/

    public interface OnBluetoothPermissionsGranted {
        void onPermissionsGranted();
    }

    public static final int PERMISSION_BLUETOOTH = 1;
    public static final int PERMISSION_BLUETOOTH_ADMIN = 2;
    public static final int PERMISSION_BLUETOOTH_CONNECT = 3;
    public static final int PERMISSION_BLUETOOTH_SCAN = 4;

    public OnBluetoothPermissionsGranted onBluetoothPermissionsGranted;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case PrintActivity.PERMISSION_BLUETOOTH:
                case PrintActivity.PERMISSION_BLUETOOTH_ADMIN:
                case PrintActivity.PERMISSION_BLUETOOTH_CONNECT:
                case PrintActivity.PERMISSION_BLUETOOTH_SCAN:
                    this.checkBluetoothPermissions(this.onBluetoothPermissionsGranted);
                    break;
            }
        }
    }

    public void checkBluetoothPermissions(OnBluetoothPermissionsGranted onBluetoothPermissionsGranted) {
        this.onBluetoothPermissionsGranted = onBluetoothPermissionsGranted;
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH}, PrintActivity.PERMISSION_BLUETOOTH);
        } else if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_ADMIN}, PrintActivity.PERMISSION_BLUETOOTH_ADMIN);
        } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, PrintActivity.PERMISSION_BLUETOOTH_CONNECT);
        } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_SCAN}, PrintActivity.PERMISSION_BLUETOOTH_SCAN);
        } else {
            this.onBluetoothPermissionsGranted.onPermissionsGranted();
        }
    }

    private BluetoothConnection selectedDevice;

    public void browseBluetoothDevice() {
        this.checkBluetoothPermissions(() -> {
            final BluetoothConnection[] bluetoothDevicesList = (new BluetoothPrintersConnections()).getList();

            if (bluetoothDevicesList != null) {
                final String[] items = new String[bluetoothDevicesList.length + 1];
                items[0] = "Default printer";
                int i = 0;
                for (BluetoothConnection device : bluetoothDevicesList) {
                    items[++i] = device.getDevice().getName();
                }

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(PrintActivity.this);
                alertDialog.setTitle("Bluetooth printer selection");
                alertDialog.setItems(
                        items,
                        (dialogInterface, i1) -> {
                            int index = i1 - 1;
                            if (index == -1) {
                                selectedDevice = null;
                            } else {
                                selectedDevice = bluetoothDevicesList[index];
                            }
                            Button button = (Button) findViewById(R.id.button_bluetooth_browse);
                            button.setText(items[i1]);
                        }
                );

                AlertDialog alert = alertDialog.create();
                alert.setCanceledOnTouchOutside(false);
                alert.show();
            }
        });

    }

    public void printBluetooth() {
        this.checkBluetoothPermissions(() -> {
            new AsyncBluetoothEscPosPrint(
                    this,
                    new AsyncEscPosPrint.OnPrintFinished() {
                        @Override
                        public void onError(AsyncEscPosPrinter asyncEscPosPrinter, int codeException) {
                            Log.e("Async.OnPrintFinished", "AsyncEscPosPrint.OnPrintFinished : An error occurred !");
                        }

                        @Override
                        public void onSuccess(AsyncEscPosPrinter asyncEscPosPrinter) {
                            Log.i("Async.OnPrintFinished", "AsyncEscPosPrint.OnPrintFinished : Print is finished !");
                        }
                    }
            )
                    .execute(this.getAsyncEscPosPrinter(selectedDevice));
        });
    }

    /*==============================================================================================
    ===========================================USB PART=============================================
    ==============================================================================================*/

    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    private final BroadcastReceiver usbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (PrintActivity.ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbManager usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
                    UsbDevice usbDevice = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (usbManager != null && usbDevice != null) {
                            new AsyncUsbEscPosPrint(
                                    context,
                                    new AsyncEscPosPrint.OnPrintFinished() {
                                        @Override
                                        public void onError(AsyncEscPosPrinter asyncEscPosPrinter, int codeException) {
                                            Log.e("Async.OnPrintFinished", "AsyncEscPosPrint.OnPrintFinished : An error occurred !");
                                        }

                                        @Override
                                        public void onSuccess(AsyncEscPosPrinter asyncEscPosPrinter) {
                                            Log.i("Async.OnPrintFinished", "AsyncEscPosPrint.OnPrintFinished : Print is finished !");
                                        }
                                    }
                            )
                                    .execute(getAsyncEscPosPrinter(new UsbConnection(usbManager, usbDevice)));
                        }
                    }
                }
            }
        }
    };

    public void printUsb() {
        UsbConnection usbConnection = UsbPrintersConnections.selectFirstConnected(this);
        UsbManager usbManager = (UsbManager) this.getSystemService(Context.USB_SERVICE);

        if (usbConnection == null || usbManager == null) {
            new AlertDialog.Builder(this)
                    .setTitle("USB Connection")
                    .setMessage("No USB printer found.")
                    .show();
            return;
        }

        PendingIntent permissionIntent = PendingIntent.getBroadcast(
                this,
                0,
                new Intent(PrintActivity.ACTION_USB_PERMISSION),
                android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S ? PendingIntent.FLAG_MUTABLE : 0
        );
        IntentFilter filter = new IntentFilter(PrintActivity.ACTION_USB_PERMISSION);
        registerReceiver(this.usbReceiver, filter);
        usbManager.requestPermission(usbConnection.getDevice(), permissionIntent);
    }

    /*==============================================================================================
    =========================================TCP PART===============================================
    ==============================================================================================*/

    public void printTcp() {
        final EditText ipAddress = (EditText) this.findViewById(R.id.edittext_tcp_ip);
        final EditText portAddress = (EditText) this.findViewById(R.id.edittext_tcp_port);


        try {
            new AsyncTcpEscPosPrint(
                    this,
                    new AsyncEscPosPrint.OnPrintFinished() {
                        @Override
                        public void onError(AsyncEscPosPrinter asyncEscPosPrinter, int codeException) {
                            Log.e("Async.OnPrintFinished", "AsyncEscPosPrint.OnPrintFinished : An error occurred !");
                        }

                        @Override
                        public void onSuccess(AsyncEscPosPrinter asyncEscPosPrinter) {
                            Log.i("Async.OnPrintFinished", "AsyncEscPosPrint.OnPrintFinished : Print is finished !");
                        }
                    }
            )
                    .execute(
                            this.getAsyncEscPosPrinter(
                                    new TcpConnection(
                                            ipAddress.getText().toString(),
                                            Integer.parseInt(portAddress.getText().toString())
                                    )
                            )
                    );
        } catch (NumberFormatException e) {
            new AlertDialog.Builder(this)
                    .setTitle("Invalid TCP port address")
                    .setMessage("Port field must be an integer.")
                    .show();
            e.printStackTrace();
        }
    }

    private void addToSales(Long BillNumber,
                            int TockenNumber,
                            Double TotalAmount,
                            String DateTime,
                            String paymentMethod,
                            String customerName,
                            String customerEmail,
                            String customerPhone
    ) {
        Sale sale = new Sale(
                0L,          // ID (auto-generated by Room, can pass 0 here)
                BillNumber,      // Bill Number
                TockenNumber,        // Token Number
                TotalAmount,       // Total Amount
                DateTime,
                paymentMethod,
                cartItemList,
                customerName,
                customerEmail,
                customerPhone

        );

        saleRepository.insertSale(sale);
        button.setEnabled(true);
        SuccessDialogFragment dialog = SuccessDialogFragment.newInstance("Bill Saved Successfully!");
        dialog.show(getSupportFragmentManager(), "success_dialog");


    }

    /*==============================================================================================
    ===================================ESC/POS PRINTER PART=========================================
    ==============================================================================================*/

    /**
     * Asynchronous printing
     */
    @SuppressLint("SimpleDateFormat")
    public AsyncEscPosPrinter getAsyncEscPosPrinter(DeviceConnection printerConnection) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        AsyncEscPosPrinter printer = new AsyncEscPosPrinter(printerConnection, 203, 48f, 32);

        // Generate a random token number and bill number
        StringBuilder receiptContent = new StringBuilder();
        receiptContent

                .append("[L]\n")
                .append("[C]<font size='big'>BEAN BARREL</font>\n")

                .append("[C]Tel: +91 92077 78777\n")
                .append("[C]Email: supportus@beanbarrel.in\n")
                .append("[C]================================\n")
                .append("[L]\n")
                .append("[L]Date & Time: ").append(format.format(new Date())).append("\n")
                .append("[L]Token: ").append(tokenNumber).append(customerName.getText()).append("\n")
                .append("[L]Bill: ").append(billNumber).append("\n")
                .append("[C]================================\n")
                .append("[L]\n");

        // Cart items section
        for (CartItem item : cartItemList) {
            String itemName = item.getItem().getItemName();
            double itemPrice = item.getItem().getItemPrice();
            int quantity = item.getQuantity();
            double totalItemPrice = itemPrice * quantity;

            receiptContent.append("[L]<b>").append(itemName).append("</b>[R]").append(String.format("%.2f₹", totalItemPrice)).append("\n")
                    .append("[L]  + Quantity: ").append(quantity).append("\n")
                    .append("[L]\n");
        }

        receiptContent.append("[C]--------------------------------\n");

        // Calculate subtotal only
        double subtotal = cartItemList.stream()
                .mapToDouble(cartItem -> cartItem.getItem().getItemPrice() * cartItem.getQuantity())
                .sum();
//        addToSales(billNumber,
//                tokenNumber,
//                subtotal,
//                format.format(new Date()),
//                CartItemStore.INSTANCE.getPaymentMethod(),
//                customerName.getText().toString(),
//                customerEmail.getText().toString(),
//                customerPhone.getText().toString()
//        );
        // Totals section without tax
        receiptContent.append("[R]TOTAL PRICE :[R]").append(String.format("%.2f₹", subtotal)).append("\n")
                .append("[L]\n")
                .append("[C]================================\n")
                .append("[L]\n");

        // Footer section with thank you note
        receiptContent.append("[C]Thank you for shopping with us!\n");
        receiptContent.append("[C]Powered by www.zeezaglobal.com");

        // Adding Token number section
        receiptContent.append("\n\n")
                .append("[C]<font size='big'>CUSTOMER TOKEN</font>\n")
                .append("[C]================================\n")
                .append("[C]<font size='big'>Token Number: ").append(tokenNumber).append("</font>\n")
                .append("[C]================================\n");

        // Add Date and Time under Token Number
        receiptContent.append("[C]<font size='normal'>Date & Time: ").append(format.format(new Date())).append("</font>\n")
                .append("[L]\n");

        return printer.addTextToPrint(receiptContent.toString());
    }
}

