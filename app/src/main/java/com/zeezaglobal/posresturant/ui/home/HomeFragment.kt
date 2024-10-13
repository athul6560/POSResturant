package com.zeezaglobal.posresturant.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zeezaglobal.posresturant.Adapters.CartAdapter
import com.zeezaglobal.posresturant.Adapters.GridAdapter
import com.zeezaglobal.posresturant.Adapters.ItemAdapter
import com.zeezaglobal.posresturant.Application.POSApp
import com.zeezaglobal.posresturant.Entities.Item
import com.zeezaglobal.posresturant.Repository.GroupRepository
import com.zeezaglobal.posresturant.Repository.ItemRepository
import com.zeezaglobal.posresturant.ViewModel.AddNewViewModel
import com.zeezaglobal.posresturant.ViewmodelFactory.POSViewModelFactory
import com.zeezaglobal.posresturant.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    private lateinit var addNewViewModel: AddNewViewModel
    private var _binding: FragmentHomeBinding? = null
    private lateinit var itemRecyclerView: RecyclerView
    private lateinit var adapter: GridAdapter
    private val sharedPrefFile = "com.zeezaglobal.posresturant.PREFERENCE_FILE"
    private val binding get() = _binding!!
    private lateinit var cartRecyclerView: RecyclerView
    private lateinit var cartAdapter: CartAdapter
    private lateinit var subtotalTextView: TextView
    private lateinit var taxTextView: TextView
    private lateinit var totalTextView: TextView
    private lateinit var clearCart: RelativeLayout
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

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        // Find TextViews for subtotal, tax, and total
        subtotalTextView = binding.textView8
        taxTextView = binding.textView9
        totalTextView = binding.textView10
        itemRecyclerView = binding.homeRecyclerView
        cartRecyclerView = binding.cartRecyclerView
        clearCart = binding.clearCartRelativeLayout

        cartRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        cartAdapter = CartAdapter(emptyList())
        cartRecyclerView.adapter = cartAdapter
        // Set up adapter with a click listener to save the clicked item to SharedPreferences
        adapter = GridAdapter(emptyList()) { selectedItem ->
            // Save the clicked item to SharedPreferences
            saveToSharedPreferences(selectedItem)
        }
        itemRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2) // 2 columns
        itemRecyclerView.adapter = adapter

        addNewViewModel.items.observe(viewLifecycleOwner, Observer { itemList ->
            adapter.updateItems(itemList)
        })
        // Set onClickListener for clear cart button
        clearCart.setOnClickListener {
            clearCartFn()
        }

        return root
    }

    private fun clearCartFn() {
        val sharedPref = requireActivity().getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        val editor = sharedPref.edit()

        // Remove the "itemList" from SharedPreferences
        editor.remove("itemList")
        editor.apply()

        // Clear the cart items in the UI
        cartAdapter.updateItems(emptyList())

        // Reset subtotal, tax, and total TextViews
        subtotalTextView.text = "₹0.00"
        taxTextView.text = "₹0.00"
        totalTextView.text = "₹0.00"

        // Show a toast message to confirm
        Toast.makeText(requireContext(), "Cart cleared", Toast.LENGTH_SHORT).show()
    }

    private fun saveToSharedPreferences(item: Item) {
        val sharedPref = requireActivity().getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        val editor = sharedPref.edit()

        // Retrieve the existing list from SharedPreferences (as JSON)
        val existingItemsJson = sharedPref.getString("itemList", null)

        // Convert JSON back to a List<Item> or initialize a new list
        val itemListType = object : TypeToken<MutableList<Item>>() {}.type
        val itemList: MutableList<Item> = if (existingItemsJson != null) {
            Gson().fromJson(existingItemsJson, itemListType)
        } else {
            mutableListOf()
        }

        // Add the new item to the list if it doesn't already exist
        if (!itemList.any { it.itemId == item.itemId }) {
            itemList.add(item)
            // Convert the updated list back to JSON
            val updatedItemsJson = Gson().toJson(itemList)

            // Store the updated list in SharedPreferences
            editor.putString("itemList", updatedItemsJson)
            editor.apply()

            // Show a toast as feedback
            Toast.makeText(requireContext(), "${item.itemName} added to preferences", Toast.LENGTH_SHORT).show()

        } else {
            // Show a toast if the item is already in the list
            Toast.makeText(requireContext(), "${item.itemName} is already in the list", Toast.LENGTH_SHORT).show()
        }
        loadCartFromSharedPreferences()

    }

    private fun loadCartFromSharedPreferences() {
        val sharedPref = requireActivity().getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        val itemsJson = sharedPref.getString("itemList", null)

        if (itemsJson != null) {
            // Convert JSON to a List<Item>
            val itemType = object : TypeToken<List<Item>>() {}.type
            val itemList: List<Item> = Gson().fromJson(itemsJson, itemType)

            // Update the CartAdapter with the retrieved items
            cartAdapter.updateItems(itemList)
            calculateTotals(itemList)
        }
    }
    private fun calculateTotals(itemList: List<Item>) {
        // Calculate subtotal
        val subtotal = itemList.sumOf { it.itemPrice }

        // Calculate tax (18% of subtotal)
        val tax = subtotal * 0.18

        // Calculate total (subtotal + tax)
        val total = subtotal + tax

        // Update the TextViews
        subtotalTextView.text = String.format("₹%.2f", subtotal)
        taxTextView.text = String.format("₹%.2f", tax)
        totalTextView.text = String.format("₹%.2f", total)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        clearSharedPreferences()
        _binding = null
    }

    private fun clearSharedPreferences() {
        val sharedPref = requireActivity().getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        val editor = sharedPref.edit()

        // Clear the stored preferences
        editor.clear()
        editor.apply()
    }
}