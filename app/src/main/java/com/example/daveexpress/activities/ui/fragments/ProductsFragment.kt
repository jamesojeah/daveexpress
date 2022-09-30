package com.example.daveexpress.activities.ui.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ProgressBar
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.airbnb.lottie.LottieAnimationView
import com.example.daveexpress.R
import com.example.daveexpress.activities.AddProductActivity
import com.example.daveexpress.adapters.MyProductsListAdapter
import com.example.daveexpress.data.MainViewModelFactory
import com.example.daveexpress.data.MyRepository
import com.example.daveexpress.data.MyViewModel

import com.example.daveexpress.databinding.FragmentProductsBinding
import com.example.daveexpress.firestore.FirestoreClass
import com.example.daveexpress.models.Product
import com.example.daveexpress.utils.ProductListViewState
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.timerTask

class ProductsFragment : BaseFragment() {

    private var _binding: FragmentProductsBinding? = null
//    val viewModel: MyViewModel by viewModels()
    lateinit var myViewModel: MyViewModel

    var productsListArray: ArrayList<Product> = ArrayList()
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        // If we want to use the option menu in fragment we need to add it.
        setHasOptionsMenu(true)

//        val model = ViewModelProvider.AndroidViewModelFactory(application = ).create(MyViewModel::class.java)
        myViewModel = ViewModelProvider(this, factory = MainViewModelFactory(repository = MyRepository())).get(MyViewModel::class.java)
        Log.d("Viewmodel created successfully", "Viewmodel created successfully")

        super.onCreate(savedInstanceState)
    }

    override fun onResume() {

        myViewModel.viewState.observe(viewLifecycleOwner){viewState ->
            updateUI(viewState)
        }

        getProductListFromFireStore()

        super.onResume()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentProductsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val swipe: SwipeRefreshLayout = binding.swipeRefreshLayout
        swipe.setOnRefreshListener {
            getProductListFromFireStore()
            Timer().schedule(timerTask {
                swipe.isRefreshing = false
            }, 2000)

        }


        return root
    }

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_product_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        when (id) {

            R.id.action_add_product -> {

                // TODO Step 9: Launch the SettingActivity on click of action item.
                // START
                startActivity(Intent(activity,AddProductActivity::class.java))
                // END
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun updateUI(viewState: ProductListViewState){
        _binding?.tvNoProductsFound?.visibility = View.GONE

        when (viewState){

            is ProductListViewState.Loading ->{
                _binding?.rvMyProductItems?.visibility = View.GONE
                _binding?.tvNoProductsFound?.visibility = View.GONE
                _binding?.productProgressBar?.visibility = View.VISIBLE
            }
            is ProductListViewState.Content -> {

                    _binding?.tvNoProductsFound?.visibility = View.GONE
                    _binding?.productProgressBar?.visibility = View.INVISIBLE

            }
        }
    }

     fun getProductListFromFireStore() {
         _binding?.rvMyProductItems?.visibility = View.GONE
         _binding?.tvNoProductsFound?.visibility = View.GONE

//         myViewModel.getProducts()

         myViewModel.allProducts().observe(viewLifecycleOwner) {
             Log.d("List of Products", "Viewmodel was used successfully")

             val adapterProducts =
                 MyProductsListAdapter(requireActivity(), it, this@ProductsFragment)

             if (it.size > 0) {
                 _binding?.rvMyProductItems?.adapter = adapterProducts
                 _binding?.tvNoProductsFound?.visibility = View.GONE
                 _binding?.rvMyProductItems?.layoutManager = LinearLayoutManager(activity)
                 _binding?.rvMyProductItems?.setHasFixedSize(true)

                 _binding?.rvMyProductItems?.visibility = View.VISIBLE

             } else {
                 _binding?.rvMyProductItems?.visibility = View.GONE
                 _binding?.productProgressBar?.visibility = View.INVISIBLE
                 _binding?.tvNoProductsFound?.visibility = View.VISIBLE
             }
         }


//        FirestoreClass().getProductsList(this)
//         hideProgressDialog()
    }

    /**
     * A function that will call the delete function of FirestoreClass that will delete the product added by the user.
     *
     * @param productID To specify which product need to be deleted.
     */
    fun deleteProduct(productID: String) {

        showAlertDialogToDeleteProduct(productID)
    }

    /**
     * A function to notify the success result of product deleted from cloud firestore.
     */
    fun productDeleteSuccess() {

        // Hide the progress dialog
        hideProgressDialog()

        Toast.makeText(
            requireActivity(),
            resources.getString(R.string.product_delete_success_message),
            Toast.LENGTH_SHORT
        ).show()

        // Get the latest products list from cloud firestore.
        getProductListFromFireStore()
    }
    // END

    /**
     * A function to show the alert dialog for the confirmation of delete product from cloud firestore.
     */
    private fun showAlertDialogToDeleteProduct(productID: String) {

        val builder = AlertDialog.Builder(requireActivity())
        //set title for alert dialog
        builder.setTitle(resources.getString(R.string.delete_dialog_title))
        //set message for alert dialog
        builder.setMessage(resources.getString(R.string.delete_dialog_message))
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        //performing positive action
        builder.setPositiveButton(resources.getString(R.string.yes)) { dialogInterface, _ ->

            // Show the progress dialog.
            showProgressDialog(resources.getString(R.string.please_wait))

            // Call the function of Firestore class.
            FirestoreClass().deleteProduct(this@ProductsFragment, productID)

            dialogInterface.dismiss()
        }

        //performing negative action
        builder.setNegativeButton(resources.getString(R.string.no)) { dialogInterface, _ ->

            dialogInterface.dismiss()
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    // END
}



//Commmented Previous Code

//@param productsList Will receive the product list from cloud firestore.
//    fun successProductsListFromFireStore(productsList: ArrayList<Product>) {
//
//        if (productsList.size > 0) {
//            _binding?.rvMyProductItems?.visibility = View.VISIBLE
//            _binding?.tvNoProductsFound?.visibility = View.GONE
////
//            _binding?.rvMyProductItems?.layoutManager = LinearLayoutManager(activity)
//            _binding?.rvMyProductItems?.setHasFixedSize(true)
//
//            val adapterProducts =
//                MyProductsListAdapter(requireActivity(), productsList, this@ProductsFragment )
//
//            _binding?.rvMyProductItems?.adapter = adapterProducts
//        } else {
//            _binding?.rvMyProductItems?.visibility = View.GONE
//            _binding?.tvNoProductsFound?.visibility = View.VISIBLE
//        }
//
//        }

