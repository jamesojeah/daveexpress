package com.example.daveexpress.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.daveexpress.R
import com.example.daveexpress.databinding.ActivitySettingsBinding
import com.example.daveexpress.firestore.FirestoreClass
import com.example.daveexpress.models.User
import com.example.daveexpress.utils.Constants
import com.example.daveexpress.utils.GlideLoader
import com.google.firebase.auth.FirebaseAuth

class SettingsActivity : BaseActivity(), View.OnClickListener {
    private lateinit var mUserDetails: User

    private lateinit var binding: ActivitySettingsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()

        binding.tvEdit.setOnClickListener(this@SettingsActivity)
        binding.btnLogout.setOnClickListener(this@SettingsActivity)
        binding.llAddress.setOnClickListener(this)
        binding.btnAboutUs.setOnClickListener(this)
        // END
    }

    override fun onResume() {
        super.onResume()
        getUserDetails()
    }
    // TODO Step 2: Create a function to setup action bar.
    // START
    /**
     * A function for actionBar Setup.
     */
    private fun setupActionBar() {

        setSupportActionBar(binding.toolbarSettingsActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        binding.toolbarSettingsActivity.setNavigationOnClickListener { onBackPressed() }
    }
    // END

    // TODO Step 4: Create a function to get the user details from firestore.
    // START
    /**
     * A function to get the user details from firestore.
     */
    private fun getUserDetails() {

        // Show the progress dialog
        showProgressDialog(resources.getString(R.string.please_wait))

        // Call the function of Firestore class to get the user details from firestore which is already created.
        FirestoreClass().getUserDetails(this@SettingsActivity)
    }
    // END

    // TODO Step 6: Create a function to receive the success result.
    // START
    /**
     * A function to receive the user details and populate it in the UI.
     */
    fun userDetailsSuccess(user: User) {
        mUserDetails = user

        // TODO Step 9: Set the user details to UI.
        hideProgressDialog()

        // Load the image using the Glide Loader class.
        GlideLoader(this@SettingsActivity).loadUserPicture(user.image, binding.ivUserPhoto)

        binding.tvName.text = "${user.firstName} ${user.lastName}"
        binding.tvGender.text = user.gender
        binding.tvEmail.text = user.email
        binding.tvMobileNumber.text = "${user.mobile}"

        if (user.admin == 0){
            binding.tvAdmin.visibility = View.INVISIBLE
        } else{
            binding.tvAdmin.visibility = View.VISIBLE
        }
        // END
    }

    override fun onClick(v: View?) {
        if(v != null){
            when(v.id){
                R.id.tv_edit -> {
                    val intent = Intent(this@SettingsActivity, UserProfileActivity::class.java)
                    intent.putExtra(Constants.EXTRA_USER_DETAILS, mUserDetails)
                    startActivity(intent)
                }
                R.id.btn_logout -> {
                    FirebaseAuth.getInstance().signOut()

                    val intent = Intent(this@SettingsActivity, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
                R.id.ll_address -> {
                    val intent = Intent(this@SettingsActivity, AddressListActivity::class.java)
                    startActivity(intent)
                }
                R.id.btn_about_us ->{
                    val intent = Intent(this@SettingsActivity, AboutUs::class.java)
                    startActivity(intent)
                }

            }
        }
    }
}