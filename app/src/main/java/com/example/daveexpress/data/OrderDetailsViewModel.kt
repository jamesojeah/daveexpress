package com.example.daveexpress.data

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.daveexpress.activities.MyOrderDetailsActivity
import com.example.daveexpress.models.Order
import com.example.daveexpress.models.Product
import com.example.daveexpress.utils.Constants

import kotlin.collections.ArrayList

class OrderDetailsViewModel(val repository: MyRepository) : ViewModel()  {
    private var myordde : Order = MyOrderDetailsActivity().myOrderDetails

    private val ordersdetails: MutableLiveData<ArrayList<Order>> by lazy {
        MutableLiveData<ArrayList<Order>>().also { data ->
               val  myOrderDetails = myordde

                val ordarray: ArrayList<Order> = ArrayList()
                ordarray.add(myOrderDetails)
                data.value = ordarray

        }
    }

    fun myOrderdetails(): MutableLiveData<ArrayList<Order>> {
        return ordersdetails
    }
}