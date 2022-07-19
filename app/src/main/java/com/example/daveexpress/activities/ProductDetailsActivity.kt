package com.example.daveexpress.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.daveexpress.R
import com.example.daveexpress.databinding.ActivityProductDetailsBinding
import com.example.daveexpress.firestore.FirestoreClass
import com.example.daveexpress.models.CartItem
import com.example.daveexpress.models.Product
import com.example.daveexpress.utils.Constants
import com.example.daveexpress.utils.GlideLoader
import java.text.SimpleDateFormat
import java.util.*

class ProductDetailsActivity : BaseActivity(), View.OnClickListener {

    private var mProductId: String = ""
private lateinit var mProductDetails: Product
    private lateinit var binding: ActivityProductDetailsBinding
    private var mProductOwnerId: String = ""
    private var mChosenSize: String = ""
    private var mStockQuantity: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProductDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar()

        if (intent.hasExtra(Constants.EXTRA_PRODUCT_ID)) {
            mProductId = intent.getStringExtra(Constants.EXTRA_PRODUCT_ID)!!
        }
//        var productOwnerId: String = ""

        if (intent.hasExtra(Constants.EXTRA_PRODUCT_OWNER_ID)) {
            mProductOwnerId = intent.getStringExtra(Constants.EXTRA_PRODUCT_OWNER_ID)!!
        }
        if (FirestoreClass().getCurrentUserID() == mProductOwnerId) {
            binding.btnAddToCart.visibility = View.GONE
            binding.btnGoToCart.visibility = View.GONE
            binding.tilSelectsize.visibility = View.GONE
            binding.tvChooseasizeHeading.visibility = View.GONE
            binding.etSelectsize.visibility = View.GONE
            binding.etSalesprice.visibility = View.INVISIBLE
            binding.rgSales.visibility = View.VISIBLE
            binding.tvSalesHeading.visibility = View.VISIBLE
            binding.tvSalesPriceHeading.visibility = View.INVISIBLE
            binding.tilSales.visibility = View.INVISIBLE
            binding.btnSubmitSaleprice.visibility = View.INVISIBLE
            binding.rbSaleNo.visibility = View.VISIBLE
            binding.rbSaleYes.visibility =View.VISIBLE

        } else {
            binding.btnAddToCart.visibility = View.VISIBLE
            binding.tvChooseasizeHeading.visibility = View.VISIBLE
            binding.tilSelectsize.visibility = View.VISIBLE
            binding.etSelectsize.visibility = View.VISIBLE
            binding.etSalesprice.visibility = View.GONE
            binding.rgSales.visibility = View.GONE
            binding.tvSalesHeading.visibility = View.GONE
            binding.tilSales.visibility = View.GONE
            binding.tvSalesPriceHeading.visibility = View.GONE
            binding.rbSaleNo.visibility = View.GONE
            binding.rbSaleYes.visibility =View.GONE
            binding.btnSubmitSaleprice.visibility = View.GONE
        }

        binding.rgSales.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.rb_sale_yes) {
                binding.tilSales.visibility = View.VISIBLE
                binding.tvSalesPriceHeading.visibility = View.VISIBLE
                binding.btnSubmitSaleprice.visibility = View.VISIBLE
            } else {
                binding.tilSales.visibility = View.GONE
                binding.tvSalesPriceHeading.visibility = View.GONE
                binding.btnSubmitSaleprice.visibility = View.GONE
            }
        }
        getProductDetails()
        binding.btnAddToCart.setOnClickListener(this)
        binding.btnGoToCart.setOnClickListener(this)
    }

    private fun setupActionBar() {

        setSupportActionBar(binding.toolbarProductDetailsActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }

        binding.toolbarProductDetailsActivity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun getProductDetails() {

        // Show the product dialog
        showProgressDialog(resources.getString(R.string.please_wait))

        // Call the function of FirestoreClass to get the product details.
        FirestoreClass().getProductDetails(this@ProductDetailsActivity, mProductId)
    }
    //A function to notify the success result of the product details based on the product id.

    fun productDetailsSuccess(product: Product) {

        mProductDetails = product
        // Hide Progress dialog.
        hideProgressDialog()

        // Populate the product details in the UI.
        GlideLoader(this@ProductDetailsActivity).loadProductPicture(
            product.image,
            binding.ivProductDetailImage
        )

        val dateFormat = "dd MMM yyyy HH:mm"
        // Create a DateFormatter object for displaying date in specified format.
        val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = product.product_datetime

        binding.tvProductDetailsTitle.text = product.title
        binding.tvProductDetailsPrice.text = "₦${product.price}"
        binding.tvProductDetailsDescription.text = product.description
        binding.tvProductDetailsAvailableQuantity.text = product.stock_quantity
        binding.tvAvailablesizesBody.text = product.available_sizes

        // TODO Step 8: Update the UI if the stock quantity is 0.
        // START
         if (product.stock_quantity.toInt() == 0) {

            // Hide Progress dialog.
            hideProgressDialog()

            // Hide the AddToCart button if the item is already in the cart.
            binding.btnAddToCart.visibility = View.GONE

            binding.tvProductDetailsAvailableQuantity.text =
                resources.getString(R.string.lbl_out_of_stock)

        binding.tvProductDetailsAvailableQuantity.setTextColor(
                ContextCompat.getColor(
                    this@ProductDetailsActivity,
                    R.color.colorSnackBarError
                )
            )
        } else {

            // There is no need to check the cart list if the product owner himself is seeing the product details.
            if (FirestoreClass().getCurrentUserID() == product.user_id) {
                // Hide Progress dialog.
                hideProgressDialog()
            } else {
                FirestoreClass().checkIfItemExistInCart(this@ProductDetailsActivity, mProductId)
            }
        }
    }


    /**
     * A function to prepare the cart item to add it to the cart.
     */
    private fun addToCart() {
        mStockQuantity = binding.tvProductDetailsAvailableQuantity.text.toString()
        mChosenSize = binding.etSelectsize.text.toString()
        val addToCart = CartItem(
            FirestoreClass().getCurrentUserID(),
            mProductOwnerId,
            mProductId,
            mProductDetails.title,
            mProductDetails.price,
            mProductDetails.image,
            Constants.DEFAULT_CART_QUANTITY,
            mStockQuantity,
            mChosenSize

        )
        // Show the progress dialog
        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().addCartItems(this@ProductDetailsActivity, addToCart)
    }
    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {

                R.id.btn_add_to_cart -> {
                    addToCart()
                }
                R.id.btn_go_to_cart -> {
                    startActivity(Intent(this@ProductDetailsActivity, CartListActivity::class.java))
                }
            }
        }
    }

    /**
     * A function to notify the success result of item added to the to cart.
     */
    fun addToCartSuccess() {
        // Hide the progress dialog.
        hideProgressDialog()

        Toast.makeText(
            this@ProductDetailsActivity,
            resources.getString(R.string.success_message_item_added_to_cart),
            Toast.LENGTH_SHORT
        ).show()

        // TODO Step 11: Change the buttons visibility once the item is added to the cart.
        // Hide the AddToCart button if the item is already in the cart.
        binding.btnAddToCart.visibility = View.GONE
        // Show the GoToCart button if the item is already in the cart. User can update the quantity from the cart list screen if he wants.
        binding.btnGoToCart.visibility = View.VISIBLE
    }

    /**
     * A function to notify the success result of item exists in the cart.
     */
    fun productExistsInCart() {

        // Hide the progress dialog.
        hideProgressDialog()

        // Hide the AddToCart button if the item is already in the cart.
        binding.btnAddToCart.visibility = View.GONE
        // Show the GoToCart button if the item is already in the cart. User can update the quantity from the cart list screen if he wants.
        binding.btnGoToCart.visibility = View.VISIBLE
    }
}