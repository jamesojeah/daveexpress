package com.example.daveexpress.activities

import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowInsets
import android.view.WindowManager
import com.example.daveexpress.activities.DashboardActivity
import com.example.daveexpress.activities.LoginActivity
import com.example.daveexpress.databinding.ActivitySplashBinding
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        @Suppress("DEPRECATION")
        Handler().postDelayed(
            {

                //Launch the Main Activity
                checkifloggedinbefore()
                finish()
            }, 2500
        )

        val typeface: Typeface = Typeface.createFromAsset(assets, "Montserrat-Bold.ttf")
        binding.tvAppName.typeface = typeface
    }

    fun checkifloggedinbefore() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            startActivity(Intent(this@SplashActivity, DashboardActivity::class.java))
        }else{
            startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
        }
    }

    }
