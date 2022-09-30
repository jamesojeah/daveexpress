package com.example.daveexpress.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.daveexpress.R
import com.example.daveexpress.adapters.AddressListAdapter
import com.example.daveexpress.adapters.CartItemsListAdapter
//import com.example.daveexpress.databinding.ActivityCartListBinding
import com.example.daveexpress.databinding.ActivityCheckoutBinding
import com.example.daveexpress.firestore.FirestoreClass
import com.example.daveexpress.models.Address
import com.example.daveexpress.models.CartItem
import com.example.daveexpress.models.Order
import com.example.daveexpress.models.Product
import com.example.daveexpress.utils.Constants


open class CheckoutActivity : BaseActivity() {

    var mAddressDetails: Address? = null
    private lateinit var mProductsList: ArrayList<Product>
  private  lateinit var mCartItemsList: ArrayList<CartItem>
    private var mSubTotal: Double = 0.0
    private var mSubTotalSale: Double = 0.0
    private var mSubTotalNosale: Double = 0.0
    private var mTotalAmount: Double = 0.0
    private lateinit var mOrderDetails: Order

    private lateinit var binding: ActivityCheckoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()
        if(intent.hasExtra(Constants.EXTRA_SELECTED_ADDRESS)){
            mAddressDetails = intent.getParcelableExtra<Address>(Constants.EXTRA_SELECTED_ADDRESS)
        }
        if (mAddressDetails != null){
            binding.tvCheckoutAddressType.text = mAddressDetails?.type
            binding.tvCheckoutFullName.text = mAddressDetails?.name
            binding.tvCheckoutAddress.text = "${mAddressDetails!!.address}, ${mAddressDetails!!.zipCode}"
            binding.tvCheckoutAdditionalNote.text = mAddressDetails?.additionalNote

            if (mAddressDetails?.otherDetails!!.isNotEmpty()) {
                binding.tvCheckoutOtherDetails.text = mAddressDetails?.otherDetails
            }
            binding.tvCheckoutMobileNumber.text = mAddressDetails?.mobileNumber
        }

        binding.btnPlaceOrder.setOnClickListener {
            val intent = Intent(this@CheckoutActivity, PaymentCardDetails::class.java)
            intent.putExtra(Constants.EXTRA_SELECTED_ADDRESS, mAddressDetails)
            startActivity(intent)
        }
        getProductList()
    }
    // END
    //A function for actionBar Setup.

    private fun setupActionBar() {

        setSupportActionBar(binding.toolbarCheckoutActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        binding.toolbarCheckoutActivity.setNavigationOnClickListener { onBackPressed() }
    }
    /**
     * A function to get product list to compare the current stock with the cart items.
     */
    open fun getProductList() {

        // Show the progress dialog.
        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().getAllProductsList(this@CheckoutActivity)
    }
    /**
     * A function to get the success result of product list.
     *
     * @param productsList
     */
    open fun successProductsListFromFireStore(productsList: ArrayList<Product>) {

        // TODO Step 8: Initialize the global variable of all product list.
        mProductsList = productsList

        // TODO Step 10: Call the function to get the latest cart items.
        getCartItemsList()

    }
    // END

    // TODO Step 9: Create a function to get the list of cart items in the activity.
    /**
     * A function to get the list of cart items in the activity.
     */
    private fun getCartItemsList() {

        FirestoreClass().getCartList(this@CheckoutActivity)
    }

    // TODO Step 11: Create a function to notify the success result of the cart items list from cloud firestore.
    // START
    /**
     * A function to notify the success result of the cart items list from cloud firestore.
     *
     * @param cartList
     */
    open fun successCartItemsList(cartList: ArrayList<CartItem>) {

        hideProgressDialog()
        // Update the stock quantity in the cart list from the product list.
        for (product in mProductsList) {
            for (cartItem in cartList) {
                if (product.productId == cartItem.product_id) {
                    cartItem.stock_quantity = product.stock_quantity
                }
            }
        }
        // Initialize the cart list.
        mCartItemsList = cartList

        // TODO Step 2: Populate the cart items in the UI.
        // START
        binding.rvCartListItems.layoutManager = LinearLayoutManager(this@CheckoutActivity)
        binding.rvCartListItems.setHasFixedSize(true)

        // TODO Step 5: Pass the required param.
        val cartListAdapter = CartItemsListAdapter(this@CheckoutActivity, mCartItemsList, false)
        binding.rvCartListItems.adapter = cartListAdapter
        // END

        // TODO Step 4: Replace the subTotal and totalAmount variables with the global variables.
        // START
        for (item in mCartItemsList) {

            val availableQuantity = item.stock_quantity.toInt()

            if (availableQuantity > 0 && item.sale_status == Constants.NO) {
                val price = item.price.toDouble()
                val quantity = item.cart_quantity.toInt()

                mSubTotalNosale += (price * quantity)
            } else if (availableQuantity > 0 && item.sale_status == Constants.YES){
                val price = item.sale_price.toDouble()
                val quantity = item.cart_quantity.toInt()

                mSubTotalSale += (price * quantity)
            }

            mSubTotal = mSubTotalNosale + mSubTotalSale
        }

        binding.tvCheckoutSubTotal.text = "₦$mSubTotal"
        // Here we have kept Shipping Charge is fixed as $10 but in your case it may cary. Also, it depends on the location and total amount.
        binding.tvCheckoutShippingCharge.text = "₦10.0"

        if (mSubTotal > 0) {
            binding.llCheckoutPlaceOrder.visibility = View.VISIBLE

            mTotalAmount = mSubTotal + 10.0
            binding.tvCheckoutTotalAmount.text = "₦$mTotalAmount"
        } else {
            binding.llCheckoutPlaceOrder.visibility = View.GONE
        }
        // END

        // TODO Step 9: Calculate the subtotal and Total Amount.
        // START
        var subTotal: Double = 0.0
        var subTotalSale: Double = 0.0
        var subTotalNoSale: Double = 0.0

        for (item in mCartItemsList) {

            val availableQuantity = item.stock_quantity.toInt()

            if (availableQuantity > 0 && item.sale_status == Constants.NO) {
                val price = item.price.toDouble()
                val quantity = item.cart_quantity.toInt()

                subTotalNoSale += (price * quantity)
            } else if(availableQuantity > 0 && item.sale_status == Constants.YES){
                val price = item.sale_price.toDouble()
                val quantity = item.cart_quantity.toInt()

                subTotalSale += (price * quantity)
            }

            subTotal = subTotalNoSale + subTotalSale
        }

        binding.tvCheckoutSubTotal.text = "₦$subTotal"

        //TODO: Look at this shipping fee code
        // Here we have kept Shipping Charge is fixed as $10 but in your case it may cary. Also, it depends on the location and total amount.
        binding.tvCheckoutShippingCharge.text = "₦10.0"

        if (subTotal > 0) {
            binding.llCheckoutPlaceOrder.visibility = View.VISIBLE

            val total = subTotal + 10
            binding.tvCheckoutTotalAmount.text = "₦$total"
        } else {
            binding.llCheckoutPlaceOrder.visibility = View.GONE
        }

    }
    /**
     * A function to prepare the Order details to place an order.
     */
   private fun placeAnOrder() {

        // Show the progress dialog.
        showProgressDialog(resources.getString(R.string.please_wait))

        // TODO Step 5: Now prepare the order details based on all the required details.
        if(mAddressDetails != null){
            mOrderDetails = Order(
                FirestoreClass().getCurrentUserID(),
                mCartItemsList,
                mAddressDetails!!,
                //TODO: Try using the user ID as a way to make the order title unique
                "My order ${System.currentTimeMillis()}",
                mCartItemsList[0].image,
                mSubTotal.toString(),
                "10.0", // The Shipping Charge is fixed as $10 for now in our case.
                mTotalAmount.toString(),
                System.currentTimeMillis()
            )

            // TODO Step 10: Call the function to place the order in the cloud firestore.
            // START
            FirestoreClass().placeOrder(this, mOrderDetails)
            // END
        }
    }

    // TODO Step 8: Create a function to notify the success result of the order placed.
    // START
    /**
     * A function to notify the success result of the order placed.
     */
    open fun orderPlacedSuccess() {
//
        FirestoreClass().updateAllDetails(this@CheckoutActivity, mCartItemsList, mOrderDetails)
    }
    open fun allDetailsUpdatedSuccessfully() {

        // TODO Step 6: Move the piece of code from OrderPlaceSuccess to here.
        // Hide the progress dialog.
        hideProgressDialog()

        Toast.makeText(this@CheckoutActivity, "Your order placed successfully.", Toast.LENGTH_SHORT)
            .show()

        val intent = Intent(this@CheckoutActivity, DashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
        // END
    }
}