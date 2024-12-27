package com.zeezaglobal.posresturant.ui.EditMenu

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
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
    private lateinit var groupAdapter: ArrayAdapter<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val application = this.application as POSApp
        val itemRepository = ItemRepository((application).database.itemDao())
        val groupRepository = GroupRepository((application).database.groupDao())
        viewModel = ViewModelProvider(
            this,
            EditMenuViewModelFactory(groupRepository, itemRepository)
        )[EditMenuViewModel::class.java]
        val spinner: Spinner = findViewById(R.id.category_spinner)
        groupAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, mutableListOf())
        groupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = groupAdapter
        viewModel.groups.observe(this) { groupList ->
            val groupNames =
                groupList.map { it.groupName } // Assuming Group has a 'groupName' field
            groupAdapter.clear()
            groupAdapter.addAll(groupNames)
            groupAdapter.notifyDataSetChanged()

            item?.let { currentItem ->
                val selectedGroup = groupList.find { it.groupId == currentItem.groupId }
                val selectedGroupName = selectedGroup?.groupName
                selectedGroupName?.let {
                    val selectedPosition = groupNames.indexOf(it)
                    if (selectedPosition >= 0) {
                        binding.categorySpinner.setSelection(selectedPosition)
                    }
                }
            }
        }
        binding.deleteBtn.setOnClickListener {
            if (::item.isInitialized) {
                viewModel.deleteItem(item)
                Toast.makeText(this, "Item Deleted Successfully", Toast.LENGTH_SHORT).show()
                finish() // Close the activity after deletion
            } else {
                Toast.makeText(this, "No item to delete", Toast.LENGTH_SHORT).show()
            }
        }
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                val selectedCategory = parent.getItemAtPosition(position).toString()
                // Handle the selected category here
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Handle case when nothing is selected (optional)
            }
        }
        // Retrieve JSON string from Intent
        val itemJson = intent.getStringExtra("EXTRA_ITEM_JSON")
        viewModel.loadGroups()
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
        val selectedGroupName = binding.categorySpinner.selectedItem.toString()
        val selectedGroupId =
            viewModel.groups.value?.find { it.groupName == selectedGroupName }?.groupId

        if (selectedGroupId != null) {
            val updatedItem = item.copy(
                itemName = binding.itemName.text.toString(),
                itemDescription = binding.itemDescription.text.toString(),
                itemPrice = binding.itemPrice.text.toString().toDouble(),
                groupId = selectedGroupId // Set the selected group ID
            )
            // Update the item in the database
            viewModel.editItem(updatedItem)
            Toast.makeText(this, "Item Updated Successfully", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Please select a valid category", Toast.LENGTH_SHORT).show()
        }
    }

    private fun populateItemData() {
        binding.itemName.setText(item.itemName)
        binding.itemDescription.setText(item.itemDescription)
        binding.itemPrice.setText(item.itemPrice.toString())

        viewModel.groups.value?.let { groupList ->
            val groupNames = groupList.map { it.groupName }
            val currentGroupId = item.groupId
            val selectedGroup = groupList.find { it.groupId == currentGroupId }
            val selectedGroupName = selectedGroup?.groupName
            val position = groupNames.indexOf(selectedGroupName)
            if (position >= 0) {
                binding.categorySpinner.setSelection(position)
            }
        }
    }
}