package com.example.daveexpress.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.daveexpress.R
import com.example.daveexpress.adapters.CartItemsListAdapter
import com.example.daveexpress.databinding.ActivityCartListBinding
import com.example.daveexpress.firestore.FirestoreClass
import com.example.daveexpress.models.CartItem
import com.example.daveexpress.models.Product
import com.example.daveexpress.utils.Constants

class CartListActivity : BaseActivity() {

    // A global variable for the product list.
    private lateinit var mProductsList: ArrayList<Product>
    // A global variable for the cart list items.
    private lateinit var mCartListItems: ArrayList<CartItem>

    private lateinit var binding: ActivityCartListBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCartListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()
        binding.btnCheckout.setOnClickListener {
            val intent = Intent(this@CartListActivity, AddressListActivity::class.java)
            intent.putExtra(Constants.EXTRA_SELECT_ADDRESS, true)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()

        //getCartItemsList()
        getProductList()
    }

    //A function for actionBar Setup.

    private fun setupActionBar() {

        setSupportActionBar(binding.toolbarCartListActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        binding.toolbarCartListActivity.setNavigationOnClickListener { onBackPressed() }
    }

    // A function to get the list of cart items in the activity.

    private fun getCartItemsList() {

        // Show the progress dialog.
        //showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().getCartList(this@CartListActivity)
    }

    // TODO Step 3: Create a function to notify the user about the item quantity updated in the cart list.
    // START
    /**
     * A function to notify the user about the item quantity updated in the cart list.
     */
    fun itemUpdateSuccess() {

        hideProgressDialog()

        getCartItemsList()
    }

    /**
     * A function to get product list to compare the current stock with the cart items.
     */
    private fun getProductList() {

        // Show the progress dialog.
        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().getAllProductsList(this@CartListActivity)
    }

    fun successProductsListFromFireStore(productsList: ArrayList<Product>) {
        hideProgressDialog()
        mProductsList = productsList

        getCartItemsList()
    }


    //A function to notify the success result of the cart items list from cloud firestore.

    fun successCartItemsList(cartList: ArrayList<CartItem>) {

        // Hide progress dialog.
        hideProgressDialog()

        // TODO Step 3: Compare the product id of product list with product id of cart items list and update the stock quantity in the cart items list from the latest product list.
        // START
        for (product in mProductsList) {
            for (cartItem in cartList) {
                if (product.product_id == cartItem.product_id) {

                    cartItem.stock_quantity = product.stock_quantity

                    if (product.stock_quantity.toInt() == 0){
                        cartItem.cart_quantity = product.stock_quantity
                    }
                }
            }
        }
        mCartListItems = cartList
        // TODO Step 6: Now onwards use the global variable of the cart list items as mCartListItems instead of cartList.
        if (mCartListItems.size > 0) {

            binding.rvCartItemsList.visibility = View.VISIBLE
            binding.llCheckout.visibility = View.VISIBLE
            binding.tvNoCartItemFound.visibility = View.GONE

            binding.rvCartItemsList.layoutManager = LinearLayoutManager(this@CartListActivity)
            binding.rvCartItemsList.setHasFixedSize(true)

            val cartListAdapter = CartItemsListAdapter(this@CartListActivity, mCartListItems, true)
            binding.rvCartItemsList.adapter = cartListAdapter

            var subTotal: Double = 0.0

            for (item in mCartListItems) {

                // TODO Step 7: Calculate the subtotal based on the stock quantity.
                // START
                val availableQuantity = item.stock_quantity.toInt()

                if (availableQuantity > 0) {
                    val price = item.price.toDouble()
                    val quantity = item.cart_quantity.toInt()

                    subTotal += (price * quantity)
                }
            }
//        if (cartList.size > 0) {
//
//            binding.rvCartItemsList.visibility = View.VISIBLE
//            binding.llCheckout.visibility = View.VISIBLE
//            binding.tvNoCartItemFound.visibility = View.GONE
//
//            binding.rvCartItemsList.layoutManager = LinearLayoutManager(this@CartListActivity)
//            binding.rvCartItemsList.setHasFixedSize(true)
//
//            val cartListAdapter = CartItemsListAdapter(this@CartListActivity, cartList)
//            binding.rvCartItemsList.adapter = cartListAdapter
//
//            var subTotal: Double = 0.0
//
//            for (item in cartList) {
//
//                val price = item.price.toDouble()
//                val quantity = item.cart_quantity.toInt()
//
//                subTotal += (price * quantity)


            binding.tvSubTotal.text = "$$subTotal"
            // Here we have kept Shipping Charge is fixed as $10 but in your case it may cary. Also, it depends on the location and total amount.
            binding.tvShippingCharge.text = "$10.0" // TODO - Change shipping charge logic

            if (subTotal > 0) {
                binding.llCheckout.visibility = View.VISIBLE

                val total = subTotal + 10 // TODO - Change Logic here
                binding.tvTotalAmount.text = "$$total"
            } else {
                binding.llCheckout.visibility = View.GONE
            }

        } else {
            binding.rvCartItemsList.visibility = View.GONE
            binding.llCheckout.visibility = View.GONE
            binding.tvNoCartItemFound.visibility = View.VISIBLE
        }

        }

    // TODO Step 5: Create a function to notify the user about the item removed from the cart list.
    // START
    /**
     * A function to notify the user about the item removed from the cart list.
     */
    fun itemRemovedSuccess() {

        hideProgressDialog()

        Toast.makeText(
            this@CartListActivity,
            resources.getString(R.string.msg_item_removed_successfully),
            Toast.LENGTH_SHORT
        ).show()

        getCartItemsList()
    }
    }
