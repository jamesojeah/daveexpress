package com.example.daveexpress.utils

import androidx.lifecycle.MutableLiveData
import com.example.daveexpress.models.Product
import java.util.ArrayList

sealed class ProductListViewState {
    object Loading: ProductListViewState()
    object Error: ProductListViewState()
    data class Content(val productList: MutableLiveData<ArrayList<Product>>) : ProductListViewState()
}