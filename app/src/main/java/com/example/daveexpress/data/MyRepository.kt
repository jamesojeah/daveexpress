package com.example.daveexpress.data

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.example.daveexpress.activities.ui.fragments.OrdersFragment
import com.example.daveexpress.activities.ui.fragments.SoldProductsFragment
import com.example.daveexpress.models.*
import com.example.daveexpress.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class MyRepository {
    private val mFireStore = FirebaseFirestore.getInstance()
    //we will get data from firebase
    private var _prod: MutableLiveData<ArrayList<Product>> = MutableLiveData<ArrayList<Product>>()
    private var _ord: MutableLiveData<ArrayList<Order>> = MutableLiveData<ArrayList<Order>>()
    private var _adminord: MutableLiveData<ArrayList<Order>> = MutableLiveData<ArrayList<Order>>()
    private var _cards: MutableLiveData<ArrayList<Cards>> = MutableLiveData<ArrayList<Cards>>()
    private var _soldproducts: MutableLiveData<ArrayList<SoldProduct>> = MutableLiveData<ArrayList<SoldProduct>>()
    private var _outofstock: MutableLiveData<ArrayList<OS>> = MutableLiveData<ArrayList<OS>>()
    /**
     * A function to get the user id of current logged user.
     */
    fun getCurrentUserID(): String {
        // An Instance of currentUser using FirebaseAuth
        val currentUser = FirebaseAuth.getInstance().currentUser

        // A variable to assign the currentUserId if it is not null or else it will be blank.
        var currentUserID = ""
        if (currentUser != null) {
            currentUserID = currentUser.uid
        }

        return currentUserID
    }

    fun getCardsList(){
        mFireStore.collection(Constants.CARDS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
//            .orderBy("product_datetime", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { document ->
                Log.e("Cards List", document.documents.toString())
                val cardsList: ArrayList<Cards> = ArrayList()
                // A for loop as per the list of documents to convert them into Products ArrayList.
                for (i in document.documents) {

                    val cards = i.toObject(Cards::class.java)!!
                    cards.cardId = i.id
                    cardsList.add(cards)
                    _cards.value = cardsList
                }
            }
            .addOnFailureListener {
                Log.d("Cards were not gotten", "Cards were not retrieved")
            }
    }

    internal var allthecards: MutableLiveData<ArrayList<Cards>>
        get() {return  _cards}
        set(value) {_cards = value}

    fun getSoldProductsList() {

        // The collection name for SOLD PRODUCTS
        mFireStore.collection(Constants.SOLD_PRODUCTS)
            .orderBy("order_date", Query.Direction.DESCENDING)
//            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .get() // Will get the documents snapshots.
            .addOnSuccessListener { document ->
                // Here we get the list of sold products in the form of documents.
                Log.e("Sold product list", document.documents.toString())

                // Here we have created a new instance for Sold Products ArrayList.
                val soldProductlist: ArrayList<SoldProduct> = ArrayList()

                // A for loop as per the list of documents to convert them into Sold Products ArrayList.
                for (i in document.documents) {

                    val soldProduct = i.toObject(SoldProduct::class.java)!!
                    soldProduct.id = i.id

                    soldProductlist.add(soldProduct)
                    _soldproducts.value = soldProductlist

                }
            }
            .addOnFailureListener { e ->
                Log.e(
             "Sold Product Error",
                    "Error while getting the list of sold products.",
                    e
                )
            }
    }

    internal var allthesoldproducts: MutableLiveData<ArrayList<SoldProduct>>
        get() {return  _soldproducts}
        set(value) {_soldproducts = value}

    fun getProductsList() {
        mFireStore.collection(Constants.PRODUCTS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .orderBy("product_datetime", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { document ->
                Log.e("Products List", document.documents.toString())
                val productsList: ArrayList<Product> = ArrayList()
                // A for loop as per the list of documents to convert them into Products ArrayList.
                for (i in document.documents) {

                    val product = i.toObject(Product::class.java)
                    product!!.productId = i.id

                    productsList.add(product)
                  _prod.value = productsList
//                    ProductsFragment().successProductsListFromFireStore(productsList)
                }
                }
            .addOnFailureListener {
                Log.d("Products wasnt gotten", "Products were not retrieved")
            }


    }

    internal var alltheproducts: MutableLiveData<ArrayList<Product>>
    get() {return  _prod}
    set(value) {_prod = value}


    fun getOutofStockList() {
        mFireStore.collection(Constants.PRODUCTS)
//            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .orderBy("product_datetime", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { document ->
                Log.e("OS List", document.documents.toString())
                val productsList: ArrayList<Product> = ArrayList()
                // A for loop as per the list of documents to convert them into Products ArrayList.
                for (i in document.documents) {

                    val product = i.toObject(Product::class.java)
                    product!!.productId = i.id

                    productsList.add(product)
                    _prod.value = productsList
//                    ProductsFragment().successProductsListFromFireStore(productsList)
                }
            }
            .addOnFailureListener {
                Log.d("OS wasnt gotten", "Products were not retrieved")
            }
    }

    internal var alltheOS: MutableLiveData<ArrayList<OS>>
        get() {return  _outofstock}
        set(value) {_outofstock = value}

    /**
     * A function to get the list of orders from cloud firestore.
     */
    fun getMyOrdersList() {
        mFireStore.collection(Constants.ORDERS)
            .orderBy("order_datetime", Query.Direction.DESCENDING)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .get() // Will get the documents snapshots.
            .addOnSuccessListener { document ->
                Log.e("orderList", document.documents.toString())
                val orderlist: ArrayList<Order> = ArrayList()

                for (i in document.documents) {

                    val orderItem = i.toObject(Order::class.java)!!
                    orderItem.id = i.id

                    orderlist.add(orderItem)
                    _ord.value = orderlist
                }

            }
            .addOnFailureListener { e ->
                Log.e("orders were not gotten", "Error while getting the orders list.", e)
            }
    }

    internal var allmyorders: MutableLiveData<ArrayList<Order>>
        get() {return  _ord}
        set(value) {_ord = value}


    fun getAllOrdersList(){
        mFireStore.collection(Constants.ORDERS)
            .orderBy("order_datetime", Query.Direction.DESCENDING)
            .get() // Will get the documents snapshots.
            .addOnSuccessListener { document ->
                Log.e("all orders for admin", document.documents.toString())
                val list: ArrayList<Order> = ArrayList()

                for (i in document.documents) {

                    val allOrdersItem = i.toObject(Order::class.java)!!
                    allOrdersItem.id = i.id

                    list.add(allOrdersItem)
                    _adminord.value = list
                }
            }
            .addOnFailureListener { e ->
                        Log.e("failed to get all orders", "Error while getting the orders list.", e)
                    }
                }

    internal var alladminorders: MutableLiveData<ArrayList<Order>>
        get() {return  _adminord}
        set(value) {_adminord = value}

}