package com.example.daveexpress.data

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.example.daveexpress.activities.ui.fragments.OrdersFragment
import com.example.daveexpress.models.Order
import com.example.daveexpress.models.Product
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