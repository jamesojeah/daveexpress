package com.example.daveexpress.activities

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.daveexpress.R
//import com.example.daveexpress.activities.databinding.ActivityDashboardBinding
import com.example.daveexpress.databinding.ActivityDashboardBinding
import com.example.daveexpress.firestore.FirestoreClass
import com.example.daveexpress.models.User

class DashboardActivity : BaseActivity() {

    private lateinit var binding: ActivityDashboardBinding

//    lateinit var user: User
//lateinit var appBarConfiguration: AppBarConfiguration
//lateinit var navController: NavController
    lateinit var navView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

//         Update the background color of the action bar as per our design requirement.
        supportActionBar?.setBackgroundDrawable(
            ContextCompat.getDrawable(
                this@DashboardActivity,
                R.drawable.app_gradient_color_background
            )
        )

         navView = binding.navView

        FirestoreClass().getUserDetails(this@DashboardActivity)



      val  navController = findNavController(R.id.nav_host_fragment_activity_dashboard)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_products,
                R.id.navigation_dashboard,
                R.id.navigation_orders,
                R.id.navigation_sold_products
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)

        navView.setupWithNavController(navController)
    }

//        FirestoreClass().getUserDetails(this@DashboardActivity)


//        setupActionBar()


//    private fun setupActionBar() {
//        setSupportActionBar(binding.toolbarDashboardActivity)
//        binding.toolbarDashboardActivity.setBackgroundResource(
//                R.drawable.app_gradient_color_background
//        )
//
//        val actionBar = supportActionBar
//        if (actionBar != null) {
//            actionBar.setDisplayHomeAsUpEnabled(true)
//        }
//    }
fun adminSuccess(user: User) {
    // Hide the progress dialog.
//    hideProgressDialog()

    if (user.admin == 0){
        navView.menu.findItem(R.id.navigation_dashboard).isVisible = true
        navView.menu.findItem(R.id.navigation_orders).isVisible = true
        navView.menu.findItem(R.id.navigation_products).isVisible = false
        navView.menu.findItem(R.id.navigation_sold_products).isVisible = false
    }else{
        navView.menu.findItem(R.id.navigation_dashboard).isVisible = true
        navView.menu.findItem(R.id.navigation_orders).isVisible = true
        navView.menu.findItem(R.id.navigation_products).isVisible = true
        navView.menu.findItem(R.id.navigation_sold_products).isVisible = true
    }
}
    override fun onBackPressed() {
        doubleBackToExit()
    }


}