package com.example.daveexpress.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.daveexpress.R
import com.example.daveexpress.adapters.CartItemsListAdapter
import com.example.daveexpress.adapters.OutofStockAdapter
import com.example.daveexpress.data.MyRepository
import com.example.daveexpress.data.OSViewModel
import com.example.daveexpress.data.OSViewModelFactory
import com.example.daveexpress.databinding.ActivityMyOrderDetailsBinding
import com.example.daveexpress.databinding.ActivityOutofStockBinding

class OutofStockActivity : AppCompatActivity() {

    lateinit var myOSViewModel: OSViewModel

    private lateinit var binding: ActivityOutofStockBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        myOSViewModel = ViewModelProvider(this, factory = OSViewModelFactory(repository = MyRepository())).get(OSViewModel::class.java)
        Log.d("OS Viewmodel created successfully", " OS Viewmodel created successfully")

        getOSList()

        binding = ActivityOutofStockBinding.inflate(layoutInflater)
        setContentView(binding.root)


        super.onCreate(savedInstanceState)

    }

    private fun getOSList(){
        myOSViewModel.myOS().observe(this){
            if (it.size>0){
                binding.rvOutofstock.visibility = View.VISIBLE
                binding.tvNoOs.visibility = View.GONE

                binding.rvOutofstock.layoutManager = LinearLayoutManager(this@OutofStockActivity)
                binding.rvOutofstock.setHasFixedSize(true)

                val OSListAdapter =
                    OutofStockAdapter(this@OutofStockActivity, it)
                binding.rvOutofstock.adapter = OSListAdapter
            }
            else {
                binding.rvOutofstock.visibility = View.GONE
                binding.tvNoOs.visibility = View.VISIBLE
            }
        }
    }
}