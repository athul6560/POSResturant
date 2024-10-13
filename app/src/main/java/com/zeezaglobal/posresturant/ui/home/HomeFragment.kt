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
import com.zeezaglobal.posresturant.Entities.CartItem
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
        cartAdapter = CartAdapter(mutableListOf()) { updatedItemList ->
            // Update the cart in your fragment or activity here
            // For example, recalculate totals based on the updated list
            calculateTotals(updatedItemList)
        }
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

        // Remove the "cartItemList" from SharedPreferences (since we are now storing CartItem instead of Item)
        editor.remove("cartItemList")
        editor.apply()

        // Clear the cart items in the UI
        cartAdapter.updateItems(emptyList())

        // Reset subtotal, tax, and total TextViews
        subtotalTextView.text = "₹0.00"
        taxTextView.text = "₹0.00"
        totalTextView.text = "₹0.00"

        // Show a toast message to confirm the cart is cleared
        Toast.makeText(requireContext(), "Cart cleared", Toast.LENGTH_SHORT).show()
    }

    private fun saveToSharedPreferences(item: Item) {
        val sharedPref = requireActivity().getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        val editor = sharedPref.edit()

        // Retrieve the existing list from SharedPreferences (as JSON)
        val existingItemsJson = sharedPref.getString("cartItemList", null)

        // Convert JSON back to a List<CartItem> or initialize a new list
        val cartItemType = object : TypeToken<MutableList<CartItem>>() {}.type
        val cartItemList: MutableList<CartItem> = if (existingItemsJson != null) {
            Gson().fromJson(existingItemsJson, cartItemType)
        } else {
            mutableListOf()
        }

        // Check if the item is already in the cart
        val existingCartItem = cartItemList.find { it.item.itemId == item.itemId }
        if (existingCartItem != null) {
            // If the item is already in the cart, update the quantity
            existingCartItem.quantity += 1
        } else {
            // If the item is not in the cart, add a new CartItem with quantity 1
            cartItemList.add(CartItem(item = item, quantity = 1))
        }

        // Convert the updated list back to JSON
        val updatedCartItemsJson = Gson().toJson(cartItemList)

        // Store the updated list in SharedPreferences
        editor.putString("cartItemList", updatedCartItemsJson)
        editor.apply()

        // Show a toast as feedback
      //  Toast.makeText(requireContext(), "${item.itemName} added to preferences", Toast.LENGTH_SHORT).show()

        // Load the cart again to update the UI
        loadCartFromSharedPreferences()

    }

    private fun loadCartFromSharedPreferences() {
        val sharedPref = requireActivity().getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        val itemsJson = sharedPref.getString("cartItemList", null)

        if (itemsJson != null) {
            // Change List<CartItem> to MutableList<CartItem>
            val cartItemType = object : TypeToken<MutableList<CartItem>>() {}.type
            val cartItemList: MutableList<CartItem> = Gson().fromJson(itemsJson, cartItemType)

            // Update the CartAdapter with the retrieved items
            cartAdapter.updateItems(cartItemList)  // Update the adapter with only items
            calculateTotals(cartItemList)
        }
        }

    private fun calculateTotals(cartItemList: List<CartItem>) {
        val subtotal = cartItemList.sumOf { it.item.itemPrice * it.quantity } // Multiply price by quantity

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