package com.example.daveexpress.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.registerForActivityResult
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.daveexpress.R
import com.example.daveexpress.adapters.AddressListAdapter
import com.example.daveexpress.databinding.ActivityAddressListBinding
import com.example.daveexpress.firestore.FirestoreClass
import com.example.daveexpress.models.Address
import com.example.daveexpress.utils.Constants
import com.example.daveexpress.utils.SwipeToDeleteCallback
import com.myshoppal.utils.SwipeToEditCallback

class AddressListActivity : BaseActivity() {

    // TODO Step 3: Declare a global variable to select the address.
    // START
    private var mSelectAddress: Boolean = false
    // END

    private lateinit var binding: ActivityAddressListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddressListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Receive the value and initialize the variable to select the address.

        if (intent.hasExtra(Constants.EXTRA_SELECT_ADDRESS)) {
            mSelectAddress =
                intent.getBooleanExtra(Constants.EXTRA_SELECT_ADDRESS, false)
        }

        setupActionBar()
        // TODO Step 5: If it is about to select the address then update the title.
        // START
        if (mSelectAddress) {
            binding.tvTitle.text = resources.getString(R.string.title_select_address)
        }
        // END
        binding.tvAddAddress.setOnClickListener {
            val intent = Intent(this@AddressListActivity, AddEditAddressActivity::class.java)

            // TODO Step 12: Now to notify the address list about the latest address added we need to make neccessary changes as below.
            // START
            getResult.launch(intent)
            // END

        }
        getAddressList()
    }

    private val getResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if (it.resultCode == Activity.RESULT_OK){
            getAddressList()
        } else if (it.resultCode == Activity.RESULT_CANCELED) {
            // A log is printed when user close or cancel the image selection.
            Log.e("Request Cancelled", "To add the address.")
        }
    }

    private fun setupActionBar() {

        setSupportActionBar(binding.toolbarAddressListActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        binding.toolbarAddressListActivity.setNavigationOnClickListener { onBackPressed() }
    }


    override fun onResume() {
        super.onResume()
//        getAddressList()
    }

    /**
     * A function to get the list of address from cloud firestore.
     */
    private fun getAddressList() {

        // Show the progress dialog.
        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().getAddressesList(this@AddressListActivity)
    }
    /**
     * A function to get the success result of address list from cloud firestore.
     *
     * @param addressList
     */
    fun successAddressListFromFirestore(addressList: ArrayList<Address>) {

        // Hide the progress dialog
        hideProgressDialog()


        // TODO Step 5: Populate the address list in the UI.
        // START
        if (addressList.size > 0) {

            binding.rvAddressList.visibility = View.VISIBLE
            binding.tvNoAddressFound.visibility = View.GONE

            binding.rvAddressList.layoutManager = LinearLayoutManager(this@AddressListActivity)
            binding.rvAddressList.setHasFixedSize(true)

            val addressAdapter = AddressListAdapter(this@AddressListActivity, addressList, mSelectAddress)
            binding.rvAddressList.adapter = addressAdapter

            if (!mSelectAddress){
                val editSwipeHandler = object : SwipeToEditCallback(this) {
                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                        // TODO Step 7: Call the notifyEditItem function of the adapter class.
                        // START
                        val adapter = binding.rvAddressList.adapter as AddressListAdapter
                        adapter.notifyEditItem(
                            this@AddressListActivity,
                            viewHolder.adapterPosition
                        )
                        // END
                    }
                }
                val editItemTouchHelper = ItemTouchHelper(editSwipeHandler)
                editItemTouchHelper.attachToRecyclerView(binding.rvAddressList)

                val deleteSwipeHandler = object : SwipeToDeleteCallback(this) {
                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                        // Show the progress dialog.
                        showProgressDialog(resources.getString(R.string.please_wait))

                        FirestoreClass().deleteAddress(
                            this@AddressListActivity,
                            addressList[viewHolder.adapterPosition].id
                        )
                    }
                }
                val deleteItemTouchHelper = ItemTouchHelper(deleteSwipeHandler)
                deleteItemTouchHelper.attachToRecyclerView(binding.rvAddressList)
            }
            // TODO Step 3: Add the swipe to edit feature.
            // START


        } else {
            binding.rvAddressList.visibility = View.GONE
            binding.tvNoAddressFound.visibility = View.VISIBLE
        }
        // END
    }
    /**
     * A function notify the user that the address is deleted successfully.
     */
    fun deleteAddressSuccess() {

        // Hide progress dialog.
        hideProgressDialog()

        Toast.makeText(
            this@AddressListActivity,
            resources.getString(R.string.err_your_address_deleted_successfully),
            Toast.LENGTH_SHORT
        ).show()

        getAddressList()
    }
}