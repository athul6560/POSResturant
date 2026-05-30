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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.zeezaglobal.posresturant.Application.POSApp
import com.zeezaglobal.posresturant.Entities.Group
import com.zeezaglobal.posresturant.Entities.Item
import com.zeezaglobal.posresturant.R
import com.zeezaglobal.posresturant.Utils.ExcelParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "UploadActivity"

class UploadActivity : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar
    private lateinit var uploadBtn: Button

    private val excelPickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri = result.data?.data
            if (uri != null) {
                Log.d(TAG, "File selected: $uri")
                handleExcelUpload(uri)
            } else {
                Log.w(TAG, "File picker returned OK but uri is null")
            }
        } else {
            Log.d(TAG, "File picker cancelled (resultCode=${result.resultCode})")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)

        progressBar = findViewById(R.id.progressBar)
        uploadBtn = findViewById(R.id.button2)

        uploadBtn.setOnClickListener {
            Log.d(TAG, "Upload button clicked — opening file picker")
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                addCategory(Intent.CATEGORY_OPENABLE)
            }
            excelPickerLauncher.launch(Intent.createChooser(intent, "Select Excel Menu File"))
        }
    }

    private fun handleExcelUpload(uri: Uri) {
        Log.d(TAG, "handleExcelUpload() called with uri: $uri")
        uploadBtn.isEnabled = false
        progressBar.visibility = View.VISIBLE
        progressBar.progress = 0

        val application = application as POSApp
        val groupDao = application.database.groupDao()
        val itemDao = application.database.itemDao()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                withContext(Dispatchers.Main) { progressBar.progress = 10 }

                Log.d(TAG, "Starting ExcelParser.parse()")
                val menuMap = ExcelParser.parse(this@UploadActivity, uri)
                Log.d(TAG, "ExcelParser returned ${menuMap.size} categories")

                withContext(Dispatchers.Main) { progressBar.progress = 40 }

                if (menuMap.isEmpty()) {
                    Log.e(TAG, "menuMap is empty — nothing to import")
                    withContext(Dispatchers.Main) {
                        progressBar.visibility = View.GONE
                        uploadBtn.isEnabled = true
                        Toast.makeText(this@UploadActivity, "No data found. Check column headers (CATEGORY, ITEM NAME).", Toast.LENGTH_LONG).show()
                    }
                    return@launch
                }

                Log.d(TAG, "Clearing existing groups and items...")
                groupDao.deleteAllGroups() // cascades to items
                withContext(Dispatchers.Main) { progressBar.progress = 60 }

                var totalItems = 0
                menuMap.forEach { (categoryName, itemList) ->
                    Log.d(TAG, "Inserting group: \"$categoryName\" with ${itemList.size} items")
                    val groupId = groupDao.insertGroupAndGetId(Group(groupName = categoryName)).toInt()
                    itemList.forEach { (name, price) ->
                        itemDao.insertItem(Item(groupId = groupId, itemName = name, itemDescription = "", itemPrice = price))
                        totalItems++
                    }
                }

                Log.d(TAG, "Import complete — $totalItems items inserted")
                withContext(Dispatchers.Main) {
                    progressBar.progress = 100
                    progressBar.visibility = View.GONE
                    uploadBtn.isEnabled = true
                    Toast.makeText(this@UploadActivity, "Imported $totalItems items successfully!", Toast.LENGTH_LONG).show()
                }

            } catch (e: Exception) {
                Log.e(TAG, "Import failed: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    uploadBtn.isEnabled = true
                    Toast.makeText(this@UploadActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
