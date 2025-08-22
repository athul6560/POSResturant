package com.zeezaglobal.posresturant.ui.UploadExcel

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.zeezaglobal.posresturant.Entities.Item
import com.zeezaglobal.posresturant.Entities.MenuItem
import com.zeezaglobal.posresturant.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UploadActivity : AppCompatActivity() {
    private val PICK_EXCEL_REQUEST_CODE = 100
    private lateinit var progressBar: ProgressBar
    private lateinit var ItemListAfterLoading: List<Item>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_upload)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        progressBar = findViewById(R.id.progressBar)
        val uploadBtn = findViewById<Button>(R.id.button2)
        uploadBtn.setOnClickListener {
            pickExcelFile()
        }
    }

    private fun pickExcelFile() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        startActivityForResult(
            Intent.createChooser(intent, "Select Excel File"),
            PICK_EXCEL_REQUEST_CODE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_EXCEL_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                handleExcelUpload(uri)
            }
        }
    }

    private fun handleExcelUpload(uri: Uri) {
        progressBar.visibility = View.VISIBLE
        CoroutineScope(Dispatchers.IO).launch {
            try {
                withContext(Dispatchers.Main) {
                    progressBar.progress = 50
                }
             /*   val menuItems = parseExcelFile(this@UploadActivity, uri)
              //  ItemListAfterLoading = convertmenuItemtoItem(menuItems)
                withContext(Dispatchers.Main) {
                    progressBar.progress = 100
                    progressBar.visibility = View.GONE
                    Toast.makeText(
                        this@UploadActivity,
                        "Uploaded ${menuItems.size} items!",
                        Toast.LENGTH_LONG
                    ).show()
                }*/
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    Toast.makeText(this@UploadActivity, "Error: ${e.message}", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }

   /* private fun convertmenuItemtoItem(menuItems: List<MenuItem>): List<Item> {

    }*/


}