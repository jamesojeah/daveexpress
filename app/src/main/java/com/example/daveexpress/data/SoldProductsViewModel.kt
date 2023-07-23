package com.example.daveexpress.data

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.daveexpress.models.Cards
import com.example.daveexpress.models.SoldProduct
import java.util.ArrayList

class SoldProductsViewModel(val repository: MyRepository): ViewModel() {

    private val soldproducts: MutableLiveData<ArrayList<SoldProduct>> by lazy {
        MutableLiveData<ArrayList<SoldProduct>>().also { data ->
            getSoldProducts()
            repository.allthesoldproducts = data

//            _viewState.postValue(ProductListViewState.Content(data))
        }
    }

    fun mySoldProducts(): MutableLiveData<ArrayList<SoldProduct>> {
        return soldproducts
    }

    fun getSoldProducts(){
        repository.getSoldProductsList()
    }
}