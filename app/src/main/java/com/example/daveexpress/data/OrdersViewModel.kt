package com.example.daveexpress.data

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.daveexpress.models.Order
import com.example.daveexpress.models.Product
import com.example.daveexpress.utils.ProductListViewState
import java.util.ArrayList

class OrdersViewModel(val repository: MyRepository) : ViewModel() {
//    private val _viewState = MutableLiveData<ProductListViewState>()
//    val viewState: MutableLiveData<ProductListViewState>
//        get() = _viewState
//
//    init {
//        _viewState.postValue(ProductListViewState.Loading)
//    }

    private val orders: MutableLiveData<ArrayList<Order>> by lazy {
        MutableLiveData<ArrayList<Order>>().also { data ->
            getOrders()
            repository.allmyorders = data

//            _viewState.postValue(ProductListViewState.Content(data))
        }

    }
    fun myOrders(): MutableLiveData<ArrayList<Order>> {
        return orders
    }

    fun getOrders(){
        repository.getMyOrdersList()
    }


    private val adminorders: MutableLiveData<ArrayList<Order>> by lazy {
        MutableLiveData<ArrayList<Order>>().also { data ->
            getadminOrders()
            repository.alladminorders = data

//            _viewState.postValue(ProductListViewState.Content(data))
        }

    }

    fun adminOrders(): MutableLiveData<ArrayList<Order>> {
        return adminorders
    }

    fun getadminOrders(){
        repository.getAllOrdersList()
    }

}