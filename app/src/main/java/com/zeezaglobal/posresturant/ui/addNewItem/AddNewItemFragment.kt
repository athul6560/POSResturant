package com.zeezaglobal.posresturant.ui.addNewItem

import android.R
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zeezaglobal.posresturant.Adapters.ItemAdapter
import com.zeezaglobal.posresturant.Application.POSApp
import com.zeezaglobal.posresturant.Repository.GroupRepository
import com.zeezaglobal.posresturant.Repository.ItemRepository
import com.zeezaglobal.posresturant.ViewModel.AddNewViewModel
import com.zeezaglobal.posresturant.ViewmodelFactory.POSViewModelFactory
import com.zeezaglobal.posresturant.databinding.FragmentAddNewBinding


class AddNewItemFragment : Fragment() {

    private var _binding: FragmentAddNewBinding? = null
    private lateinit var addNewViewModel: AddNewViewModel
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    // Declare the EditTexts
    private lateinit var itemNameEditText: EditText
    private lateinit var itemDescriptionEditText: EditText
    private lateinit var itemPriceEditText: EditText
    private var selectedGroupId: Int? = null
    private lateinit var itemRecyclerView: RecyclerView
    private lateinit var itemAdapter: ItemAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val application = requireActivity().application as POSApp
        val groupRepository = GroupRepository((application).database.groupDao())
        val itemRepository = ItemRepository((application).database.itemDao())

        val posViewModelFactory = POSViewModelFactory(groupRepository,itemRepository)
        addNewViewModel = ViewModelProvider(this, posViewModelFactory).get(
            AddNewViewModel::class.java
        )
        _binding = FragmentAddNewBinding.inflate(inflater, container, false)
        // Initialize EditTexts
        itemNameEditText = binding.itemName
        itemDescriptionEditText = binding.itemDescription
        itemPriceEditText = binding.itemPrice
        itemRecyclerView = binding.itemRecyclerView // Initialize the RecyclerView

        // Set up RecyclerView
        itemRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        itemAdapter = ItemAdapter(emptyList())
        itemRecyclerView.adapter = itemAdapter

        // Set up the ImageButton click listener
        val imageButton: ImageButton = binding.imageButton
        imageButton.setOnClickListener {
            showCategoryPopup()
        }


        val root: View = binding.root
        val categorySpinner: Spinner = binding.categorySpinner
        // Observe groups from the ViewModel
        addNewViewModel.groups.observe(viewLifecycleOwner, Observer { groupList ->
            val groupNames = groupList.map { it.groupName } // Assuming Group has a property named groupName
            val adapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_item, groupNames)
            adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
            categorySpinner.adapter = adapter
        })
        addNewViewModel.items.observe(viewLifecycleOwner, Observer { itemList ->
           itemAdapter.updateItems(itemList)
        })

        // Spinner item selection listener
        categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedGroupId = position+1
                val selectedGroup = categorySpinner.selectedItem as String

            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Handle no selection if needed
            }
        }
        // Initialize submit button
        val submitButton: Button = binding.button
        submitButton.setOnClickListener {
            // Collect the data from EditTexts
            val itemName = itemNameEditText.text.toString()
            val itemDescription = itemDescriptionEditText.text.toString()
            val itemPriceText = itemPriceEditText.text.toString()

            // Validate inputs
            if (itemName.isBlank() || itemDescription.isBlank() || itemPriceText.isBlank() || selectedGroupId == null) {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Parse the price to Double
            val itemPrice = itemPriceText.toDoubleOrNull()
            if (itemPrice == null) {
                Toast.makeText(requireContext(), "Invalid price format", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Add the item to the group
            addNewViewModel.addItemToGroup(selectedGroupId!!, itemName, itemDescription, itemPrice)

            // Show a success message
           // Toast.makeText(requireContext(), "Item '$itemName' added successfully", Toast.LENGTH_SHORT).show()

            // Clear the EditTexts after submission
            itemNameEditText.text.clear()
            itemDescriptionEditText.text.clear()
            itemPriceEditText.text.clear()
        }

        return root
    }

    private fun showCategoryPopup() {
        // Inflate the custom layout for the popup
        val inflater = LayoutInflater.from(requireContext())
        val popupView = inflater.inflate(com.zeezaglobal.posresturant.R.layout.dialog_add_category, null)

        // Create the AlertDialog
        val alertDialog = AlertDialog.Builder(requireContext())
            .setView(popupView)
            .setCancelable(true)
            .create()

        // Set up the EditText and Button in the popup
        val categoryEditText: EditText = popupView.findViewById(com.zeezaglobal.posresturant.R.id.categoryEditText)
        val addCategoryButton: Button = popupView.findViewById(com.zeezaglobal.posresturant.R.id.addCategoryButton)

        // Handle the button click to insert the category
        addCategoryButton.setOnClickListener {
            val categoryName = categoryEditText.text.toString()

            if (categoryName.isNotEmpty()) {
                // Insert the category (you can call a ViewModel method to insert it into the database)
                addNewViewModel.addGroup(categoryName)
                alertDialog.dismiss()
            } else {
                Toast.makeText(requireContext(), "Please enter a category name", Toast.LENGTH_SHORT).show()
            }
        }

        // Show the dialog
        alertDialog.show()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}