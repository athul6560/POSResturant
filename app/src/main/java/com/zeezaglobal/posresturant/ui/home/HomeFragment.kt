package com.zeezaglobal.posresturant.ui.home

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zeezaglobal.posresturant.Adapters.CartAdapter
import com.zeezaglobal.posresturant.Adapters.GridAdapter
import com.zeezaglobal.posresturant.Adapters.HorizondalAdapter
import com.zeezaglobal.posresturant.Application.POSApp
import com.zeezaglobal.posresturant.Entities.CartItem
import com.zeezaglobal.posresturant.Repository.CartItemStore
import com.zeezaglobal.posresturant.Repository.GroupRepository
import com.zeezaglobal.posresturant.Repository.ItemRepository
import com.zeezaglobal.posresturant.Utils.SharedPreferencesHelper
import com.zeezaglobal.posresturant.ViewModel.AddNewViewModel
import com.zeezaglobal.posresturant.ViewmodelFactory.POSViewModelFactory
import com.zeezaglobal.posresturant.databinding.FragmentHomeBinding
import com.zeezaglobal.posresturant.ui.printModule.PrintActivity

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
    private lateinit var horizondalrecyclerView: RecyclerView
    private lateinit var horizondaladapter: HorizondalAdapter
    private lateinit var CheckoutButton: Button
    private lateinit var searchProduct: EditText

    private val listOfCartItems: MutableList<CartItem> = mutableListOf()

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
        CheckoutButton = binding.checkoutButton
        searchProduct = binding.searchProduct
        sharedPreferencesHelper = SharedPreferencesHelper(requireContext())
        horizondalrecyclerView = binding.groupRv
        horizondalrecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        // Initialize adapter with touch handler (onItemClick lambda)


        horizondaladapter = HorizondalAdapter(emptyList()) { clickedItem ->
            Toast.makeText(requireContext(), "Clicked", Toast.LENGTH_SHORT).show()
        }
        // Initialize item list
        addNewViewModel.groups.observe(viewLifecycleOwner, Observer { groupList ->
            var groupNames = groupList.map { it.groupName } // Assuming Group has a property named groupName
            horizondaladapter.updateItems(groupNames)
        })

        CheckoutButton.setOnClickListener{
            CartItemStore.cartItemList = listOfCartItems
startActivity(Intent(requireContext(),PrintActivity::class.java))
        }

        horizondalrecyclerView.adapter = horizondaladapter
        cartRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        cartAdapter = CartAdapter(
            listOfCartItems,
            onQuantityChanged = { updateCart(it) },
            onItemAdded = { addItemToCart(it) },
            onItemSubtracted = { subtractItemFromCart(it) }
        )
        cartRecyclerView.adapter = cartAdapter
        // Set up adapter with a click listener to save the clicked item to SharedPreferences
        adapter = GridAdapter(emptyList()) { selectedItem ->
            // Check if the item already exists in the cart
            val existingCartItem = listOfCartItems.find { it.item == selectedItem }

            if (existingCartItem != null) {
                // Item already exists in the cart, update its quantity
                existingCartItem.quantity += 1
            } else {
                // Item is not in the cart, add a new CartItem with quantity 1
                listOfCartItems.add(CartItem(selectedItem, 1))
            }

            updateCart(listOfCartItems)
           // sharedPreferencesHelper.saveCartItemToSharedPreferences(selectedItem)
         //   loadCartFromSharedPreferences()
        }
        itemRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2) // 2 columns
        itemRecyclerView.adapter = adapter

        addNewViewModel.items.observe(viewLifecycleOwner, Observer { itemList ->
            adapter.updateItems(itemList)
        })
// Implement search functionality
        searchProduct.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // Call filter method on adapter whenever search query changes
                adapter.filterItems(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        // Set onClickListener for clear cart button
        clearCart.setOnClickListener {
            clearCartFn()
        }

        return root
    }

    private fun subtractItemFromCart(cartItem: CartItem) {
        // Find the item in listOfCartItems and decrease quantity or remove
        val existingItem = listOfCartItems.find { it.item == cartItem.item }
        if (existingItem != null) {
            if (existingItem.quantity > 1) {
                existingItem.quantity-- // Decrease quantity if more than 1
            } else {
                listOfCartItems.remove(existingItem) // Remove item if quantity is 1
            }

            // Refresh the adapter to reflect changes
            adapter.notifyDataSetChanged()
            updateCart(listOfCartItems) // Update cart UI
        }
    }

    private fun addItemToCart(cartItem: CartItem) {
        // Update the quantity in listOfCartItems
        val existingItem = listOfCartItems.find { it.item == cartItem.item }
        if (existingItem != null) {
            existingItem.quantity++ // Increase quantity if item exists
        } else {
            listOfCartItems.add(cartItem.apply { quantity = 1 }) // Add new item with quantity 1
        }


        // Refresh the adapter to reflect changes
        adapter.notifyDataSetChanged()
        updateCart(listOfCartItems) // Update cart UI
    }

    private fun updateCart(listOfCartItems: List<CartItem>) {
        cartAdapter.updateItems(listOfCartItems)
        calculateTotals(listOfCartItems)

    }

    private fun clearCartFn() {
        listOfCartItems.clear()
        sharedPreferencesHelper.clearCart()
        cartAdapter.updateItems(emptyList())
        subtotalTextView.text = "₹0.00"
        taxTextView.text = "₹0.00"
        totalTextView.text = "₹0.00"
        Toast.makeText(requireContext(), "Cart cleared", Toast.LENGTH_SHORT).show()
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