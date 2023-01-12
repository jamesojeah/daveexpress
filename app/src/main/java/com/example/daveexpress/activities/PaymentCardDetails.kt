package com.example.daveexpress.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager.VERTICAL
import com.example.daveexpress.R
import com.example.daveexpress.adapters.CardRecyclerViewAdapter
import com.example.daveexpress.data.CardDetailsViewModelFactory
import com.example.daveexpress.data.CardViewModel
import com.example.daveexpress.data.MyRepository
import com.example.daveexpress.data.OrderViewModelFactory
import com.example.daveexpress.databinding.ActivityPaymentCardDetailsBinding
import com.example.daveexpress.db.CardEntity
import com.example.daveexpress.firestore.FirestoreClass
import com.example.daveexpress.models.*
import com.example.daveexpress.utils.Constants
import com.example.daveexpress.utils.Tools

import com.google.android.material.textfield.TextInputEditText

class PaymentCardDetails : BaseActivity(), CardRecyclerViewAdapter.RowClickListener {

    var mycarddetails : Cards = Cards()

    lateinit var recyclerViewAdapter: CardRecyclerViewAdapter
    lateinit var viewModel: CardViewModel

    private lateinit var binding: ActivityPaymentCardDetailsBinding

    private  var mAddressDetails: Address? = null
    private lateinit var mProductsList: ArrayList<Product>
    private  lateinit var mCartItemsList: ArrayList<CartItem>
    private var mSubTotal: Double = 0.0
    private var mSubTotalSale: Double = 0.0
    private var mSubTotalNosale: Double = 0.0

    private var mTotalAmount: Double = 0.0
    private var mOrderStatus: String = "Pending"
    private lateinit var mOrderDetails: Order

    private var card_number: TextView? = null
    private var card_expire: TextView? = null
    private var card_cvv: TextView? = null
    private var card_name: TextView? = null

    private var et_card_number: TextInputEditText? = null
    private var et_expire: TextInputEditText? = null
    private var et_cvv: TextInputEditText? = null
    private var et_name: TextInputEditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentCardDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar()
        if(intent.hasExtra(Constants.EXTRA_SELECTED_ADDRESS)){
            mAddressDetails = intent.getParcelableExtra<Address>(Constants.EXTRA_SELECTED_ADDRESS)
            Log.d("intent has extra", "intent has extra")
        }else{
            Log.d("intent doesnt have extra", "intent doesnt have extra")
        }

        card_number = findViewById<View>(R.id.card_number) as TextView?
        card_expire = findViewById<View>(R.id.card_expire) as TextView?
        card_cvv = findViewById<View>(R.id.card_cvv) as TextView?
        card_name = findViewById<View>(R.id.card_name) as TextView?
        et_card_number = findViewById<View>(R.id.et_card_number) as TextInputEditText?
        et_expire = findViewById<View>(R.id.et_expire) as TextInputEditText?
        et_cvv = findViewById<View>(R.id.et_cvv) as TextInputEditText?
        et_name = findViewById<View>(R.id.et_name) as TextInputEditText?

        binding.etCardNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, count: Int) {
                if (charSequence.toString().trim { it <= ' ' }.length == 0) {
                    card_number!!.text = "**** **** **** ****"
                } else {
                    val number: String =
                        Tools.insertPeriodically(charSequence.toString().trim { it <= ' ' }, " ", 4)
                    card_number!!.text = number
                }
            }

            override fun afterTextChanged(editable: Editable) {}
        })

        et_expire!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, count: Int) {
                if (charSequence.toString().trim { it <= ' ' }.length == 0) {
                    card_expire!!.text = "MM/YY"
                } else {
                    val exp: String =
                        Tools.insertPeriodically(charSequence.toString().trim { it <= ' ' }, "/", 2)
                    card_expire!!.text = exp
                }
            }

            override fun afterTextChanged(editable: Editable) {}
        })
        et_cvv!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, count: Int) {
                if (charSequence.toString().trim { it <= ' ' }.length == 0) {
                    card_cvv!!.text = "***"
                } else {
                    card_cvv!!.text = charSequence.toString().trim { it <= ' ' }
                }
            }

            override fun afterTextChanged(editable: Editable) {}
        })
        et_name!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, count: Int) {
                if (charSequence.toString().trim { it <= ' ' }.length == 0) {
                    card_name!!.text = "Your Name"
                } else {
                    card_name!!.text = charSequence.toString().trim { it <= ' ' }
                }
            }

            override fun afterTextChanged(editable: Editable) {}
        })
//        initToolbar()


        binding.rvMyCards.layoutManager = LinearLayoutManager(this@PaymentCardDetails)
        binding.rvMyCards.setHasFixedSize(true)

         recyclerViewAdapter = CardRecyclerViewAdapter(this@PaymentCardDetails)
        binding.rvMyCards.adapter = recyclerViewAdapter
        val divider = DividerItemDecoration(applicationContext, VERTICAL)
        binding.rvMyCards.addItemDecoration(divider)


        getProductList()

//        viewModel = ViewModelProviders.of(this).get(CardViewModel::class.java)
        viewModel = ViewModelProvider(this, factory = CardDetailsViewModelFactory(repository = MyRepository())).get(CardViewModel::class.java)
        viewModel.myCards().observe(this, Observer {
            if (it.size>0){
                binding.tvNoCardsFound.visibility = View.GONE
                recyclerViewAdapter.setListData(ArrayList(it))
                recyclerViewAdapter.notifyDataSetChanged()
            }else{
                binding.rvMyCards.visibility = View.GONE
                binding.tvNoCardsFound.visibility = View.VISIBLE
            }

        })

        binding.paymentProceed.setOnClickListener {

//                placeAnOrder()
//
//            if (binding.paymentProceed.text.equals("PROCEED")) {
//                val card = CardEntity(0, name, cardnumber, exp, cvv)
//                viewModel.insertCardInfo(card)
//
//
//            }else{
//                val card = CardEntity(binding.etName.getTag(binding.etName.id).toString().toInt(),
//                    name, cardnumber, exp, cvv)
//                viewModel.updateCardInfo(card)
//            }

//            binding.etName.setText("")
//            binding.etCardNumber.setText("")
//            binding.etExpire.setText("")
//            binding.etCvv.setText("")
        }

        binding.saveCard.setOnClickListener {
            savecardtofirestore()
        }
    }
     fun getProductList() {

        // Show the progress dialog.
        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().getAllProductsList(this@PaymentCardDetails)
    }

     fun successProductsListFromFireStore(productsList: ArrayList<Product>) {

        // TODO Step 8: Initialize the global variable of all product list.
        mProductsList = productsList

        // TODO Step 10: Call the function to get the latest cart items.
        getCartItemsList()

    }
    private fun getCartItemsList() {

        FirestoreClass().getCartList(this@PaymentCardDetails)
    }

     fun successCartItemsList(cartList: ArrayList<CartItem>) {

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


//
        if (mSubTotal > 0) {
//            binding.llCheckoutPlaceOrder.visibility = View.VISIBLE

            mTotalAmount = mSubTotal + 10.0
//            binding.tvCheckoutTotalAmount.text = "$$mTotalAmount"
        } else {
//            binding.llCheckoutPlaceOrder.visibility = View.GONE
        }
//        // END

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

//        binding.tvCheckoutSubTotal.text = "$$subTotal"
//
//        //TODO: Look at this shipping fee code
//        // Here we have kept Shipping Charge is fixed as $10 but in your case it may cary. Also, it depends on the location and total amount.
//        binding.tvCheckoutShippingCharge.text = "$10.0"
//
         if (subTotal > 0) {
//            binding.llCheckoutPlaceOrder.visibility = View.VISIBLE

             val total = subTotal + 10
             mTotalAmount = total
//            binding.tvCheckoutTotalAmount.text = "$$total"
//        } else {
//            binding.llCheckoutPlaceOrder.visibility = View.GONE
//        }

         }
     }
    private fun placeAnOrder() {

        // Show the progress dialog.
        showProgressDialog(resources.getString(R.string.please_wait))
        Log.d("place order progress dialog", "place order progress dialog")
        // TODO Step 5: Now prepare the order details based on all the required details.
        if(mAddressDetails != null) {
            Log.d("address is not null", "address is not null")
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
                System.currentTimeMillis(),
                ordered_size = "",
                mOrderStatus
            )

            // TODO Step 10: Call the function to place the order in the cloud firestore.
            // START
            Log.d("about to place order", "about to place order")
            FirestoreClass().placeOrder(this, mOrderDetails)
            // END
        }else{
            Log.d("address is null", "address is null")
        }
    }

   fun orderPlacedSuccess() {
//
        FirestoreClass().updateAllDetails(this@PaymentCardDetails, mCartItemsList, mOrderDetails)
    }

     fun allDetailsUpdatedSuccessfully() {

        // TODO Step 6: Move the piece of code from OrderPlaceSuccess to here.
        // Hide the progress dialog.
        hideProgressDialog()

        Toast.makeText(this@PaymentCardDetails, "Your order placed successfully.", Toast.LENGTH_SHORT)
            .show()

        val intent = Intent(this@PaymentCardDetails, DashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
        // END
    }
    private fun setupActionBar() {

        setSupportActionBar(binding.toolbarCardDetails)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        binding.toolbarCardDetails.setNavigationOnClickListener { onBackPressed() }
    }

    override fun onDeleteUserClickListener(card: Cards) {
//        viewModel.deleteCardInfo(card)
        //delete card from firebase
    }

    override fun onItemClickListener(card: Cards) {
       binding.etName.setText(card.cardname)
        binding.etCardNumber.setText(card.cardnumber)
        binding.etExpire.setText(card.expirydate)
        binding.etCvv.setText(card.cvv)
    }


    private fun savecardtofirestore(){

        val cards = Cards(
            FirestoreClass().getCurrentUserID(),
            binding.etCardNumber.text.toString().trim { it <= ' ' },
            binding.etName.text.toString().trim { it <= ' ' },
            binding.etCvv.text.toString().trim { it <= ' ' },
            binding.etExpire.text.toString().trim { it <= ' ' },
      cardtype = "Visa for now, work on this later",
            cardId = ""

        )

        FirestoreClass().uploadCards(this, cards)
    }

    fun cardUploadSuccess(){
//        hideProgressDialog()
        Toast.makeText(
            this@PaymentCardDetails,
            resources.getString(R.string.cardsave_success_message),
            Toast.LENGTH_SHORT
        ).show()

    }
}
