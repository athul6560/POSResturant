package com.zeezaglobal.posresturant.ui.addNewItem

import android.Manifest
import android.R
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.zeezaglobal.posresturant.Adapters.ItemAdapter
import com.zeezaglobal.posresturant.Adapters.ItemEditListener
import com.zeezaglobal.posresturant.Application.POSApp
import com.zeezaglobal.posresturant.Entities.Group
import com.zeezaglobal.posresturant.Entities.Item
import com.zeezaglobal.posresturant.Repository.GroupRepository
import com.zeezaglobal.posresturant.Repository.ItemRepository
import com.zeezaglobal.posresturant.ViewModel.AddNewViewModel
import com.zeezaglobal.posresturant.ViewmodelFactory.POSViewModelFactory
import com.zeezaglobal.posresturant.databinding.BottomSheetAddItemBinding
import com.zeezaglobal.posresturant.databinding.FragmentAddNewBinding
import com.zeezaglobal.posresturant.ui.EditMenu.EditMenuActivity
import java.io.File
import java.util.UUID


class AddNewItemFragment : Fragment(), ItemEditListener {

    private var _binding: FragmentAddNewBinding? = null
    private lateinit var addNewViewModel: AddNewViewModel
    private val binding get() = _binding!!
    private var sheetBinding: BottomSheetAddItemBinding? = null
    private var selectedGroupId: Int? = null
    private var latestGroups: List<Group> = emptyList()
    private var pendingImagePath: String? = null
    private lateinit var itemRecyclerView: RecyclerView
    private lateinit var itemAdapter: ItemAdapter

    private val excelPickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri: Uri = result.data?.data ?: return@registerForActivityResult
            handleExcelUpload(uri)
        }
    }

    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        launchCropper(includeCamera = granted)
        if (!granted) {
            Toast.makeText(
                requireContext(),
                "Camera permission denied — you can still pick a photo from gallery",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private val cropImageLauncher = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            val croppedUri = result.uriContent ?: return@registerForActivityResult
            val savedPath = saveImageToInternalStorage(croppedUri)
            if (savedPath != null) {
                pendingImagePath = savedPath
                showItemPhotoPreview(savedPath)
            } else {
                Toast.makeText(requireContext(), "Failed to save photo", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "Failed to crop photo", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val application = requireActivity().application as POSApp
        val groupRepository = GroupRepository((application).database.groupDao())
        val itemRepository = ItemRepository((application).database.itemDao())

        val posViewModelFactory = POSViewModelFactory(groupRepository, itemRepository)
        addNewViewModel = ViewModelProvider(this, posViewModelFactory).get(
            AddNewViewModel::class.java
        )
        _binding = FragmentAddNewBinding.inflate(inflater, container, false)

        itemRecyclerView = binding.itemRecyclerView
        itemRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        itemAdapter = ItemAdapter(emptyList(), this)
        itemRecyclerView.adapter = itemAdapter

        addNewViewModel.groups.observe(viewLifecycleOwner, Observer { groupList ->
            latestGroups = groupList
            refreshCategorySpinner()
        })
        addNewViewModel.items.observe(viewLifecycleOwner, Observer { itemList ->
            itemAdapter.updateItems(itemList)
            binding.emptyStateText.visibility = if (itemList.isEmpty()) View.VISIBLE else View.GONE
        })

        binding.fabAddItem.setOnClickListener {
            showAddItemBottomSheet()
        }

        return binding.root
    }

    private fun refreshCategorySpinner() {
        val sb = sheetBinding ?: return
        val groupNames = latestGroups.map { it.groupName }
        val adapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_item, groupNames)
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        sb.categorySpinner.adapter = adapter
        selectedGroupId = latestGroups.firstOrNull()?.groupId
    }

    private fun showAddItemBottomSheet() {
        val dialog = BottomSheetDialog(requireContext())
        val sb = BottomSheetAddItemBinding.inflate(LayoutInflater.from(requireContext()))
        sheetBinding = sb
        dialog.setContentView(sb.root)
        dialog.setOnDismissListener { sheetBinding = null }

        pendingImagePath = null
        sb.itemPhotoContainer.setOnClickListener {
            val cameraGranted = ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED

            if (cameraGranted) {
                launchCropper(includeCamera = true)
            } else {
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }

        refreshCategorySpinner()
        sb.categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedGroupId = latestGroups.getOrNull(position)?.groupId
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        sb.addCategoryButton.setOnClickListener {
            val categoryName = sb.categoryEditText.text.toString()
            if (categoryName.isNotEmpty()) {
                addNewViewModel.addGroup(categoryName)
                sb.categoryEditText.text.clear()
            } else {
                Toast.makeText(requireContext(), "Please enter a category name", Toast.LENGTH_SHORT).show()
            }
        }

        sb.uploadMenuButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                addCategory(Intent.CATEGORY_OPENABLE)
            }
            excelPickerLauncher.launch(Intent.createChooser(intent, "Select Excel Menu File"))
        }

        sb.button.setOnClickListener {
            val itemName = sb.itemName.text.toString()
            val itemDescription = sb.itemDescription.text.toString()
            val itemPriceText = sb.itemPrice.text.toString()

            if (itemName.isBlank() || itemDescription.isBlank() || itemPriceText.isBlank() || selectedGroupId == null) {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val itemPrice = itemPriceText.toDoubleOrNull()
            if (itemPrice == null) {
                Toast.makeText(requireContext(), "Invalid price format", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            addNewViewModel.addItemToGroup(selectedGroupId!!, itemName, itemDescription, itemPrice, pendingImagePath)
            Toast.makeText(requireContext(), "Item '$itemName' added successfully", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun launchCropper(includeCamera: Boolean) {
        cropImageLauncher.launch(
            CropImageContractOptions(
                uri = null,
                cropImageOptions = CropImageOptions(
                    imageSourceIncludeCamera = includeCamera,
                    imageSourceIncludeGallery = true,
                    fixAspectRatio = true,
                    aspectRatioX = 1,
                    aspectRatioY = 1
                )
            )
        )
    }

    private fun showItemPhotoPreview(path: String) {
        val sb = sheetBinding ?: return
        sb.itemPhotoPlaceholder.visibility = View.GONE
        sb.itemPhotoPreview.visibility = View.VISIBLE
        Glide.with(this).load(File(path)).into(sb.itemPhotoPreview)
    }

    private fun saveImageToInternalStorage(uri: Uri): String? {
        return try {
            val imagesDir = File(requireContext().filesDir, "item_images").apply { mkdirs() }
            val destFile = File(imagesDir, "${UUID.randomUUID()}.jpg")
            requireContext().contentResolver.openInputStream(uri)?.use { input ->
                destFile.outputStream().use { output -> input.copyTo(output) }
            }
            destFile.absolutePath
        } catch (e: Exception) {
            null
        }
    }

    private fun handleExcelUpload(uri: Uri) {
        val uploadBtn = sheetBinding?.uploadMenuButton ?: return
        uploadBtn.isEnabled = false
        uploadBtn.text = "Importing..."
        addNewViewModel.importFromExcel(requireContext(), uri) { itemCount ->
            val btn = sheetBinding?.uploadMenuButton
            btn?.isEnabled = true
            btn?.text = "Or Upload Menu from Excel"
            if (itemCount >= 0) {
                Toast.makeText(requireContext(), "Imported $itemCount items successfully!", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(requireContext(), "Import failed. Check the file format.", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        sheetBinding = null
        _binding = null
    }

    override fun onEditItem(item: Item, position: Int) {

        val gson = Gson()
        val itemJson = gson.toJson(item)

        // Pass JSON string via Intent
        val intent = Intent(requireContext(), EditMenuActivity::class.java).apply {
            putExtra("EXTRA_ITEM_JSON", itemJson)
        }
        startActivity(intent)

    }
}
