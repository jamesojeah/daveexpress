package com.example.daveexpress.data

import android.system.Os
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.daveexpress.models.OS

class OSViewModel (val repository: MyRepository): ViewModel() {
    private val outofstockproducts: MutableLiveData<ArrayList<OS>> by lazy{
        MutableLiveData<ArrayList<OS>>().also { data ->
            getOS()
            repository.alltheOS = data
        }
    }

    fun myOS(): MutableLiveData<ArrayList<OS>>{
        return outofstockproducts
    }

    fun getOS(){
        repository.getOutofStockList()
    }
}