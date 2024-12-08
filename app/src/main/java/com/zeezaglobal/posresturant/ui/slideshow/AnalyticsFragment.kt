package com.zeezaglobal.posresturant.ui.slideshow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.zeezaglobal.posresturant.R
import com.zeezaglobal.posresturant.databinding.FragmentAnalyticsBinding
import com.zeezaglobal.posresturant.ui.customVies.SalesProgressView


class AnalyticsFragment : Fragment() {

    private var _binding: FragmentAnalyticsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val analyticsViewModel =
            ViewModelProvider(this).get(AnalyticsViewModel::class.java)

        _binding = FragmentAnalyticsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val salesView = root.findViewById<SalesProgressView>(R.id.salesProgressView)
        salesView.setSalesData(cashSales = 500, upiSales = 300, creditCardSales = 200)

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}