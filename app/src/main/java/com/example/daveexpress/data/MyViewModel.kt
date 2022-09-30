package com.example.daveexpress.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.daveexpress.models.Product
import com.example.daveexpress.utils.ProductListViewState
import java.util.ArrayList

class MyViewModel(val repository: MyRepository) : ViewModel() {

private val _viewState = MutableLiveData<ProductListViewState>()
    val viewState: MutableLiveData<ProductListViewState>
    get() = _viewState

init {
    _viewState.postValue(ProductListViewState.Loading)
}

    private val products: MutableLiveData<ArrayList<Product>> by lazy {
        MutableLiveData<ArrayList<Product>>().also { data ->
            getProducts()
            repository.alltheproducts = data

            _viewState.postValue(ProductListViewState.Content(data))
        }

    }

    fun allProducts(): MutableLiveData<ArrayList<Product>> {
        return products
    }

   fun getProducts(){
       repository.getProductsList()
    }
}

