package com.example.daveexpress.activities.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.daveexpress.R
import com.example.daveexpress.activities.OutofStockActivity
import com.example.daveexpress.adapters.MyOrdersListAdapter
import com.example.daveexpress.adapters.SoldProductsListAdapter
import com.example.daveexpress.data.*
import com.example.daveexpress.databinding.FragmentProductsBinding
import com.example.daveexpress.databinding.FragmentSoldProductsBinding
import com.example.daveexpress.firestore.FirestoreClass
import com.example.daveexpress.models.SoldProduct


class SoldProductsFragment : BaseFragment() {

    lateinit var mySoldProductsViewModel: SoldProductsViewModel

    private var _binding: FragmentSoldProductsBinding? = null

    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        mySoldProductsViewModel = ViewModelProvider(this, factory = SoldProductsViewModelFactory(repository = MyRepository())).get(SoldProductsViewModel::class.java)
        Log.d("Sold Products Viewmodel created successfully", " Sold products Viewmodel created successfully")


        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        _binding = FragmentSoldProductsBinding.inflate(inflater, container, false)
        val root: View = binding.root

       _binding?.btnGoToOS?.setOnClickListener {
            val intent = Intent(activity, OutofStockActivity::class.java)
           activity?.startActivity(intent)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // TODO Step 4: Create a function to get the list of sold products.
    // START
    private fun getSoldProductsList() {

        mySoldProductsViewModel.mySoldProducts().observe(viewLifecycleOwner){
            if (it.size>0){
                binding.rvSoldProductItems.visibility = View.VISIBLE
                binding.tvNoSoldProductsFound.visibility = View.GONE

                binding.rvSoldProductItems.layoutManager = LinearLayoutManager(activity)
                binding.rvSoldProductItems.setHasFixedSize(true)

                val mySoldProductsAdapter = SoldProductsListAdapter(requireActivity(), it)
                binding.rvSoldProductItems.adapter = mySoldProductsAdapter
            } else {
                binding.rvSoldProductItems.visibility = View.GONE
                binding.tvNoSoldProductsFound.visibility = View.VISIBLE
            }
        }
    }

    // TODO Step 5: Override the onResume function and call the function to get the list of sold products.
    override fun onResume() {
        super.onResume()


        getSoldProductsList()
    }
    // END
}