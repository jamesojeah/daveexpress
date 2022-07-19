package com.example.daveexpress.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.daveexpress.activities.LoginActivity
import com.example.daveexpress.databinding.ActivityMainBinding
import com.example.daveexpress.utils.Constants
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Create an instance of Android SharedPreferences
        val sharedPreferences =
            getSharedPreferences(Constants.DAVEEXPRESS_PREFERENCES, Context.MODE_PRIVATE)

        val username = sharedPreferences.getString(Constants.LOGGED_IN_USERNAME, "")!!
        // Set the result to the tv_main.
        binding.tvUserId.text= "The logged in user is $username."
    }
}