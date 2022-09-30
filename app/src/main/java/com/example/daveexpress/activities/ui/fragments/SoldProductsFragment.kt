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

        _binding = FragmentSoldProductsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
           binding.rvSoldProductItems.visibility = View.VISIBLE
          binding.tvNoSoldProductsFound.visibility  = View.GONE

           binding.rvSoldProductItems.layoutManager = LinearLayoutManager(activity)
          binding.rvSoldProductItems.setHasFixedSize(true)

            val soldProductsListAdapter =
                SoldProductsListAdapter(requireActivity(), soldProductsList)
           binding.rvSoldProductItems.adapter = soldProductsListAdapter
        } else {
           binding.rvSoldProductItems.visibility = View.GONE
           binding.tvNoSoldProductsFound.visibility = View.VISIBLE
        }
        // END
    }

    // TODO Step 5: Override the onResume function and call the function to get the list of sold products.
    override fun onResume() {
        super.onResume()

        getSoldProductsList()
    }
    // END
}