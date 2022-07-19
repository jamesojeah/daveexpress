package com.example.daveexpress.activities.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.daveexpress.R
import com.example.daveexpress.adapters.MyOrdersListAdapter
import com.example.daveexpress.databinding.FragmentOrdersBinding
import com.example.daveexpress.firestore.FirestoreClass
import com.example.daveexpress.models.Order

class OrdersFragment : BaseFragment() {

    private var _binding: FragmentOrdersBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentOrdersBinding.inflate(inflater, container, false)
        val root: View = binding.root


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * A function to get the list of my orders.
     */
    private fun getMyOrdersList() {
        // Show the progress dialog.
        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().getMyOrdersList(this@OrdersFragment)
    }

    // TODO Step 6: Create a function to get the success result of the my order list from cloud firestore.
    // START
    /**
     * A function to get the success result of the my order list from cloud firestore.
     *
     * @param ordersList List of my orders.
     */
    fun populateOrdersListInUI(ordersList: ArrayList<Order>) {

        // Hide the progress dialog.
        hideProgressDialog()

        // TODO Step 11: Populate the orders list in the UI.
        // START
        if (ordersList.size > 0) {

           binding.rvMyOrderItems.visibility = View.VISIBLE
            binding.tvNoOrdersFound.visibility = View.GONE

            binding.rvMyOrderItems.layoutManager = LinearLayoutManager(activity)
            binding.rvMyOrderItems.setHasFixedSize(true)

            val myOrdersAdapter = MyOrdersListAdapter(requireActivity(), ordersList)
            binding.rvMyOrderItems.adapter = myOrdersAdapter
        } else {
            binding.rvMyOrderItems.visibility = View.GONE
            binding.tvNoOrdersFound.visibility = View.VISIBLE
        }
        // END
    }

    override fun onResume() {
        super.onResume()

        getMyOrdersList()
    }
}