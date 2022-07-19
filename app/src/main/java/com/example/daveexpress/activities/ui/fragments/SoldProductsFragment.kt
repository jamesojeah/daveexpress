package com.example.daveexpress.activities.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.daveexpress.R
import com.example.daveexpress.adapters.SoldProductsListAdapter
import com.example.daveexpress.databinding.FragmentProductsBinding
import com.example.daveexpress.databinding.FragmentSoldProductsBinding
import com.example.daveexpress.firestore.FirestoreClass
import com.example.daveexpress.models.SoldProduct


class SoldProductsFragment : BaseFragment() {

    private var _binding: FragmentSoldProductsBinding? = null

    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sold_products, container, false)
    }

    // TODO Step 5: Override the onResume function and call the function to get the list of sold products.
    override fun onResume() {
        super.onResume()

        getSoldProductsList()
    }

    // TODO Step 4: Create a function to get the list of sold products.
    // START
    private fun getSoldProductsList() {
        // Show the progress dialog.
        showProgressDialog(resources.getString(R.string.please_wait))

        // Call the function of Firestore class.
        FirestoreClass().getSoldProductsList(this@SoldProductsFragment)
    }

    //Create a function to get the success result list of sold products.

    fun successSoldProductsList(soldProductsList: ArrayList<SoldProduct>) {

        // Hide Progress dialog.
        hideProgressDialog()

        // TODO Step 7: Populate the list in the RecyclerView using the adapter class.
        // START
        if (soldProductsList.size > 0) {
           _binding?.rvSoldProductItems?.visibility = View.VISIBLE
          _binding?.tvNoSoldProductsFound?.visibility  = View.GONE

           _binding?.rvSoldProductItems?.layoutManager = LinearLayoutManager(activity)
          _binding?.rvSoldProductItems?.setHasFixedSize(true)

            val soldProductsListAdapter =
                SoldProductsListAdapter(requireActivity(), soldProductsList)
           _binding?.rvSoldProductItems?.adapter = soldProductsListAdapter
        } else {
           _binding?.rvSoldProductItems?.visibility = View.GONE
           _binding?.tvNoSoldProductsFound?.visibility = View.VISIBLE
        }
        // END
    }
    // END
}