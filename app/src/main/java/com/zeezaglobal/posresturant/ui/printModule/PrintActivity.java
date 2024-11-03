package com.zeezaglobal.posresturant.ui.printModule;



import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.Manifest;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import com.dantsu.escposprinter.EscPosPrinter;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections;
import com.dantsu.escposprinter.exceptions.EscPosBarcodeException;
import com.dantsu.escposprinter.exceptions.EscPosConnectionException;
import com.dantsu.escposprinter.exceptions.EscPosEncodingException;
import com.dantsu.escposprinter.exceptions.EscPosParserException;
import com.dantsu.escposprinter.textparser.PrinterTextParserImg;
import com.zeezaglobal.posresturant.R;


public class PrintActivity extends AppCompatActivity {
    private static final int PERMISSION_BLUETOOTH = 100;
    private static final int PERMISSION_BLUETOOTH_ADMIN = 101;
    private static final int PERMISSION_BLUETOOTH_CONNECT = 102;
    private static final int PERMISSION_BLUETOOTH_SCAN = 103;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_print);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH}, PERMISSION_BLUETOOTH);
            } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_ADMIN}, PERMISSION_BLUETOOTH_ADMIN);
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, PERMISSION_BLUETOOTH_CONNECT);
            } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_SCAN}, PERMISSION_BLUETOOTH_SCAN);
            }
        }
        Button printButton = findViewById(R.id.print_btn);
        printButton.setOnClickListener(v -> checkPermissionsAndPrint());

    }

    private void checkPermissionsAndPrint() {
        try {
            print();
        } catch (EscPosConnectionException e) {
            Toast.makeText(this, "Erreur de connexion"+e.getMessage(), Toast.LENGTH_SHORT).show();
            throw new RuntimeException(e);
        } catch (EscPosEncodingException e) {
            Toast.makeText(this, "Erreur de connexion"+e.getMessage(), Toast.LENGTH_SHORT).show();

            throw new RuntimeException(e);
        } catch (EscPosBarcodeException e) {
            Toast.makeText(this, "Erreur de connexion"+e.getMessage(), Toast.LENGTH_SHORT).show();

            throw new RuntimeException(e);
        } catch (EscPosParserException e) {
            Toast.makeText(this, "Erreur de connexion"+e.getMessage(), Toast.LENGTH_SHORT).show();

            throw new RuntimeException(e);
        }
    }

    private void print() throws EscPosConnectionException, EscPosEncodingException, EscPosBarcodeException, EscPosParserException {
        EscPosPrinter printer = new EscPosPrinter(BluetoothPrintersConnections.selectFirstPaired(), 203, 48f, 32);
        printer
                .printFormattedText(
                        "[C]<img>" +"</img>\n" +
                                "[L]\n" +
                                "[C]<u><font size='big'>ORDER N°045</font></u>\n" +
                                "[L]\n" +
                                "[C]================================\n" +
                                "[L]\n" +
                                "[L]<b>BEAUTIFUL SHIRT</b>[R]9.99e\n" +
                                "[L]  + Size : S\n" +
                                "[L]\n" +
                                "[L]<b>AWESOME HAT</b>[R]24.99e\n" +
                                "[L]  + Size : 57/58\n" +
                                "[L]\n" +
                                "[C]--------------------------------\n" +
                                "[R]TOTAL PRICE :[R]34.98e\n" +
                                "[R]TAX :[R]4.23e\n" +
                                "[L]\n" +
                                "[C]================================\n" +
                                "[L]\n" +
                                "[L]<font size='tall'>Customer :</font>\n" +
                                "[L]Raymond DUPONT\n" +
                                "[L]5 rue des girafes\n" +
                                "[L]31547 PERPETES\n" +
                                "[L]Tel : +33801201456\n" +
                                "[L]\n" +
                                "[C]<barcode type='ean13' height='10'>831254784551</barcode>\n" +
                                "[C]<qrcode size='20'>https://dantsu.com/</qrcode>"
                );
    }
}
