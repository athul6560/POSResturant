package com.zeezaglobal.posresturant.ui.EditMenu

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.zeezaglobal.posresturant.Application.POSApp
import com.zeezaglobal.posresturant.Dao.ItemDao
import com.zeezaglobal.posresturant.Database.POSDatabase
import com.zeezaglobal.posresturant.Entities.Item
import com.zeezaglobal.posresturant.R
import com.zeezaglobal.posresturant.Repository.GroupRepository
import com.zeezaglobal.posresturant.Repository.ItemRepository
import com.zeezaglobal.posresturant.ViewModel.EditMenuViewModel
import com.zeezaglobal.posresturant.databinding.ActivityEditMenuBinding

class EditMenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditMenuBinding
    private lateinit var item: Item // Declare a variable to hold the Item data
    private lateinit var viewModel: EditMenuViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val application =this.application as POSApp
        val itemRepository = ItemRepository((application).database.itemDao())
        val groupRepository = GroupRepository((application).database.groupDao())
        viewModel = ViewModelProvider(this, EditMenuViewModelFactory(groupRepository, itemRepository))[EditMenuViewModel::class.java]

        // Retrieve JSON string from Intent
        val itemJson = intent.getStringExtra("EXTRA_ITEM_JSON")

        if (itemJson != null) {
            // Convert JSON string back to Item object
            val gson = Gson()
            item = gson.fromJson(itemJson, Item::class.java)

            populateItemData()
        } else {
            binding.itemName.setText("")
            binding.itemDescription.setText("")
            binding.itemPrice.setText("")
        }

        binding.button.setOnClickListener {
            updateItemData()
        }

    }

    private fun updateItemData() {
        val updatedItem = item.copy(
            itemName = binding.itemName.text.toString(),
            itemDescription = binding.itemDescription.text.toString(),
            itemPrice = binding.itemPrice.text.toString().toDouble()
        )
        viewModel.editItem(updatedItem)
        Toast.makeText(this, "Item Updated Successfully", Toast.LENGTH_SHORT).show()
    }

    private fun populateItemData() {
        binding.itemName.setText(item.itemName)
        binding.itemDescription.setText(item.itemDescription)
        binding.itemPrice.setText(item.itemPrice.toString())
    }
}