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
import com.zeezaglobal.posresturant.Utils.SharedPreferencesHelper
import com.zeezaglobal.posresturant.ViewModel.AddNewViewModel
import com.zeezaglobal.posresturant.ViewmodelFactory.POSViewModelFactory
import com.zeezaglobal.posresturant.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    private lateinit var addNewViewModel: AddNewViewModel
    private var _binding: FragmentHomeBinding? = null
    private lateinit var itemRecyclerView: RecyclerView
    private lateinit var adapter: GridAdapter
    private val binding get() = _binding!!
    private lateinit var cartRecyclerView: RecyclerView
    private lateinit var cartAdapter: CartAdapter
    private lateinit var subtotalTextView: TextView
    private lateinit var taxTextView: TextView
    private lateinit var totalTextView: TextView
    private lateinit var clearCart: RelativeLayout
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper
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
        sharedPreferencesHelper = SharedPreferencesHelper(requireContext())

        cartRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        cartAdapter = CartAdapter(mutableListOf(), sharedPreferencesHelper) { updatedItemList ->
            calculateTotals(updatedItemList)
        }
        cartRecyclerView.adapter = cartAdapter
        // Set up adapter with a click listener to save the clicked item to SharedPreferences
        adapter = GridAdapter(emptyList()) { selectedItem ->
            sharedPreferencesHelper.saveCartItemToSharedPreferences(selectedItem)
            loadCartFromSharedPreferences()
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
        sharedPreferencesHelper.clearCart()
        cartAdapter.updateItems(emptyList())
        subtotalTextView.text = "₹0.00"
        taxTextView.text = "₹0.00"
        totalTextView.text = "₹0.00"
        Toast.makeText(requireContext(), "Cart cleared", Toast.LENGTH_SHORT).show()
    }



    private fun loadCartFromSharedPreferences() {
        val cartItemList = sharedPreferencesHelper.loadCartFromSharedPreferences()
        cartAdapter.updateItems(cartItemList)
        calculateTotals(cartItemList)
        }

    private fun calculateTotals(cartItemList: List<CartItem>) {
        val subtotal = cartItemList.sumOf { it.item.itemPrice * it.quantity }
        val tax = subtotal * 0.18
        val total = subtotal + tax

        subtotalTextView.text = String.format("₹%.2f", subtotal)
        taxTextView.text = String.format("₹%.2f", tax)
        totalTextView.text = String.format("₹%.2f", total)
    }
    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }


}