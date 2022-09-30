package com.example.daveexpress.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.daveexpress.R
import com.example.daveexpress.adapters.CartItemsListAdapter
import com.example.daveexpress.data.*
import com.example.daveexpress.databinding.ActivityMyOrderDetailsBinding
import com.example.daveexpress.databinding.ActivityProductDetailsBinding
import com.example.daveexpress.firestore.FirestoreClass
import com.example.daveexpress.models.Order
import com.example.daveexpress.models.User
import com.example.daveexpress.utils.Constants
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.timerTask

class MyOrderDetailsActivity : AppCompatActivity() {

    lateinit var myOrderDetailsViewModel: OrderDetailsViewModel

    var myOrderDetails: Order = Order()
    private lateinit var binding: ActivityMyOrderDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        myOrderDetailsViewModel = ViewModelProvider(this, factory = OrderDetailsViewModelFactory(repository = MyRepository())).get(
            OrderDetailsViewModel::class.java)
        Log.d("Orders Viewmodel created successfully", " Orders Viewmodel created successfully")

        binding = ActivityMyOrderDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()

        // TODO Step 10: Get the order details through intent.
        // START
//        myOrderDetails = Order()
        if (intent.hasExtra(Constants.EXTRA_MY_ORDER_DETAILS)) {
            myOrderDetails =
                intent.getParcelableExtra<Order>(Constants.EXTRA_MY_ORDER_DETAILS)!!
        }

        FirestoreClass().getUserDetails(this@MyOrderDetailsActivity)

        myOrderDetailsViewModel.myOrderdetails().observe(this){
            setupUI(myOrderDetails)
        }

        val swipe: SwipeRefreshLayout = binding.swipeRefreshLayoutOrderDetails
        swipe.setOnRefreshListener {
            setupUI(myOrderDetails)
            Timer().schedule(timerTask {
                swipe.isRefreshing = false
            }, 2000)

        }


        when (myOrderDetails.order_status) {
            Constants.PENDING -> {
                binding.rbPending.isChecked = true
            }
            Constants.IN_PROCESS -> {
                binding.rbInprocess.isChecked = true
                binding.tvOrderStatus.setTextColor(
                    ContextCompat.getColor(
                        this@MyOrderDetailsActivity,
                        R.color.colorOrderStatusInProcess
                    )
                )
            }
            else -> {
                binding.rbDelivered.isChecked = true
                binding.tvOrderStatus.setTextColor(
                    ContextCompat.getColor(
                        this@MyOrderDetailsActivity,
                        R.color.colorOrderStatusDelivered
                    )
                )
            }
        }

        binding.btnUpdateOrderstatus.setOnClickListener {
            saveOrderStatus()
        }

    }

    private fun saveOrderStatus(){

        var orderStatus: String = when {
            binding.rbPending.isChecked -> {
                Constants.PENDING
//                myOrderDetails.order_status
            }
            binding.rbInprocess.isChecked -> {
                Constants.IN_PROCESS
//                myOrderDetails.order_status
            }
            else -> {
                Constants.DELIVERED
//                myOrderDetails.order_status
            }
        }

        binding.tvOrderStatus.text = orderStatus

        val statusHashmap = HashMap<String, Any>()
        val theOrderStatus = binding.tvOrderStatus.text.toString().trim { it <= ' ' }

        statusHashmap[Constants.ORDER_STATUS] = theOrderStatus

        FirestoreClass().updateOrderStatus(this, myOrderDetails.id ,  statusHashmap)
    }

    open fun statusUpdateSuccess(){
        Toast.makeText(this@MyOrderDetailsActivity, "Order Status updated successfully.", Toast.LENGTH_SHORT)
            .show()
    }

    fun adminSuccess(user: User) {
        if (user.admin == 0){
            binding.selectOrderstatusHeading.visibility = View.GONE
            binding.rgOrderstatus.visibility = View.GONE
            binding.rbPending.visibility = View.GONE
            binding.rbInprocess.visibility = View.GONE
            binding.rbDelivered.visibility = View.GONE
            binding.btnUpdateOrderstatus.visibility = View.GONE
        }else{
            binding.selectOrderstatusHeading.visibility = View.VISIBLE
            binding.rgOrderstatus.visibility = View.VISIBLE
            binding.rbPending.visibility = View.VISIBLE
            binding.rbInprocess.visibility = View.VISIBLE
            binding.rbDelivered.visibility = View.VISIBLE
            binding.btnUpdateOrderstatus.visibility = View.VISIBLE
        }
    }

    // TODO Step 8: Create a function to setup action bar.
    // START
    /**
     * A function for actionBar Setup.
     */
    private fun setupActionBar() {

        setSupportActionBar(binding.toolbarMyOrderDetailsActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

       binding.toolbarMyOrderDetailsActivity.setNavigationOnClickListener { onBackPressed() }
    }

    /**
     * A function to setup UI.
     *
     * @param orderDetails Order details received through intent.
     */
    private fun setupUI(orderDetails: Order) {

//        myOrderDetailsViewModel.myOrderdetails().observe(this){
            binding.tvOrderDetailsId.text = orderDetails.title

            // TODO Step 6: Set the Date in the UI.
            // START
            // Date Format in which the date will be displayed in the UI.
            val dateFormat = "dd MMM yyyy HH:mm"
            // Create a DateFormatter object for displaying date in specified format.
            val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())

            // Create a calendar object that will convert the date and time value in milliseconds to date.
            val calendar: Calendar = Calendar.getInstance()
            calendar.timeInMillis = orderDetails.order_datetime

            val orderDateTime = formatter.format(calendar.time)
            binding.tvOrderDetailsDate.text = orderDateTime
            // END

            // TODO Step 7: Set the order status based on the time for now. PLESASE CHANGE THIS CODE LATER!!

            // START
            // Get the difference between the order date time and current date time in hours.
            // If the difference in hours is 1 or less then the order status will be PENDING.
            // If the difference in hours is 2 or greater then 1 then the order status will be PROCESSING.
            // And, if the difference in hours is 3 or greater then the order status will be DELIVERED.

            val diffInMilliSeconds: Long = System.currentTimeMillis() - orderDetails.order_datetime
            val diffInHours: Long = TimeUnit.MILLISECONDS.toHours(diffInMilliSeconds)
            Log.e("Difference in Hours", "$diffInHours")

            binding.rvMyOrderItemsList.layoutManager = LinearLayoutManager(this@MyOrderDetailsActivity)
            binding.rvMyOrderItemsList.setHasFixedSize(true)

            val cartListAdapter =
                CartItemsListAdapter(this@MyOrderDetailsActivity, orderDetails.items, false)
            binding.rvMyOrderItemsList.adapter = cartListAdapter


            binding.tvOrderStatus.text = orderDetails.order_status
            binding.tvMyOrderDetailsAddressType.text = orderDetails.address.type
            binding.tvMyOrderDetailsFullName.text = orderDetails.address.name
            binding.tvMyOrderDetailsAddress.text =
                "${orderDetails.address.address}, ${orderDetails.address.zipCode}"
            binding.tvMyOrderDetailsAdditionalNote.text = orderDetails.address.additionalNote

            if (orderDetails.address.otherDetails.isNotEmpty()) {
                binding.tvMyOrderDetailsOtherDetails.visibility = View.VISIBLE
                binding.tvMyOrderDetailsOtherDetails.text = orderDetails.address.otherDetails
            } else {
                binding.tvMyOrderDetailsOtherDetails.visibility = View.GONE
            }
            binding.tvMyOrderDetailsMobileNumber.text = orderDetails.address.mobileNumber

            binding.tvOrderDetailsSubTotal.text = orderDetails.sub_total_amount
            binding.tvOrderDetailsShippingCharge.text = orderDetails.shipping_charge
            binding.tvOrderDetailsTotalAmount.text = orderDetails.total_amount
        }

    }

//}