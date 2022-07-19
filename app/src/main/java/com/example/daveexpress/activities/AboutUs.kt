package com.example.daveexpress.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.daveexpress.R
import com.example.daveexpress.databinding.AboutUsActivityBinding
import com.example.daveexpress.utils.Tools
import com.google.android.gms.maps.*
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class AboutUs: AppCompatActivity() {
    private lateinit var binding: AboutUsActivityBinding

    val recipientMail = "jamesojeah2@gmail.com"
    private var mMap: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AboutUsActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar()


        binding.btnContactus.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.data = Uri.parse("mailto:")
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(recipientMail))
            intent.putExtra(Intent.EXTRA_SUBJECT, "Support Query")
            startActivity(Intent.createChooser(intent, "Select your Email app"))
        }
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarAboutUs)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        binding.toolbarAboutUs.setNavigationOnClickListener { onBackPressed() }
    }


}