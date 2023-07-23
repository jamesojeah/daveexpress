package com.example.daveexpress.activities

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.daveexpress.R
import com.example.daveexpress.models.User
import com.example.daveexpress.databinding.ActivityLoginBinding
import com.example.daveexpress.firestore.FirestoreClass
import com.example.daveexpress.utils.Constants
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

class LoginActivity : BaseActivity(), View.OnClickListener {

    companion object {
        private const val RC_SIGN_IN = 120
    }

    private lateinit var binding: ActivityLoginBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        //Firebase Auth Instance
        mAuth = FirebaseAuth.getInstance()


        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
            // Click event assigned to Forgot Password text.
            binding.tvForgotPassword.setOnClickListener(this)
            // Click event assigned to Login button.
            binding.btnLogin.setOnClickListener(this)
            // Click event assigned to Register text.
            binding.tvRegister.setOnClickListener(this)
            binding.btnGoogleSignin.setOnClickListener(this)
        }
    }


    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //Result returned from launching the intent from GoogleSignInApi.getSignInIntent
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val exception = task.exception
            if (task.isSuccessful) {
                try {
                    //Google Signin was successful, authenticate with firebase
                    val account = task.getResult(ApiException::class.java)!!
                    Log.d("SigninActivity", "FirebaseAuth with Google" + account.id)
                    firebaseAuthWithGoogle(account.idToken!!)
                } catch (e: ApiException) {
                    //Google Signin failed, update UI appropriately
                    Log.w("SigninActivity", "Google Signin Failed", e)
                }
            } else {
                Log.w("SigninActivity", exception.toString())
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    //Signin success, update UI with the signed-in user's information
//                    val user = mAuth.currentUser

                    // Firebase registered user
//                    val firebaseUserGoogle: FirebaseUser = task.result!!.user!!
//
//                    val user = User(firebaseUserGoogle.uid,
//                        binding.etFirstName.text.toString().trim{it <= ' '},
//                        binding.etLastName.text.toString().trim{it <= ' '},
//                        binding.etEmail.text.toString().trim{it <= ' '}
//                    )

                    val currentUser = mAuth.currentUser
                    val user = User(
                        currentUser?.uid.toString(),
                        currentUser?.displayName.toString(),
                        currentUser?.displayName.toString(),
                        currentUser?.email.toString()

                    )


                    FirestoreClass().registerUserGoogle(this@LoginActivity, user)

                } else {
                    //If signin fails, display a mnessage to the user
                    Log.w("SigninActivity", "signin with credential failure")
                }
            }
    }

    fun userRegistrationGoogle() {

        Toast.makeText(
            this@LoginActivity,
            resources.getString(R.string.register_google_success),
            Toast.LENGTH_SHORT
        ).show()

        FirestoreClass().getUserDetailsGoogle(this@LoginActivity)


    }
 fun userRegistrationSuccessGoogle(user: User){
             if (user.profileCompleted == 0) {
            // If the user profile is incomplete then launch the UserProfileActivity.
            val intent = Intent(this@LoginActivity, UserProfileActivity::class.java)
            intent.putExtra(Constants.EXTRA_USER_DETAILS, user)
            startActivity(intent)
        } else {

        }
 }
        // In Login screen the clickable components are Login Button, ForgotPassword text and Register Text.
        override fun onClick(v: View?) {
            if (v != null) {
                when (v.id) {

                    R.id.tv_forgot_password -> {
                        val intent = Intent(this@LoginActivity, ForgotPasswordActivity::class.java)
                        startActivity(intent)
                    }

                    R.id.btn_login -> {

                        // Call the validate function.
                        logInRegisteredUser()
                    }

                    R.id.tv_register -> {
                        // Launch the register screen when the user clicks on the text.
                        val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                        startActivity(intent)
                    }

                    R.id.btn_google_signin -> {
                        signIn()
                    }
                }
            }
        }
        // END


        // START
        /**
         * A function to validate the login entries of a user.
         */
        private fun validateLoginDetails(): Boolean {
            return when {
                TextUtils.isEmpty(binding.etEmail.text.toString().trim { it <= ' ' }) -> {
                    showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                    false
                }
                TextUtils.isEmpty(binding.etPassword.text.toString().trim { it <= ' ' }) -> {
                    showErrorSnackBar(resources.getString(R.string.err_msg_enter_password), true)
                    false
                }
                else -> {

                    true
                }
            }
        }

        /**
         * A function to Log-In. The user will be able to log in using the registered email and password with Firebase Authentication.
         */
        private fun logInRegisteredUser() {

            if (validateLoginDetails()) {

                // Show the progress dialog.
                showProgressDialog(resources.getString(R.string.please_wait))

                // Get the text from editText and trim the space
                val email = binding.etEmail.text.toString().trim { it <= ' ' }
                val password = binding.etPassword.text.toString().trim { it <= ' ' }

                // Log-In using FirebaseAuth
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {

                            FirestoreClass().getUserDetails(this@LoginActivity)
                        } else {
                            hideProgressDialog()
                            showErrorSnackBar(task.exception!!.message.toString(), true)
                        }
                    }
            }
        }

        //A function to notify user that logged in success and get the user details from the FireStore database after authentication.

        fun userLoggedInSuccess(user: User) {

            // Hide the progress dialog.
            hideProgressDialog()

            if (user.profileCompleted == 0) {
                // If the user profile is incomplete then launch the UserProfileActivity.
                val intent = Intent(this@LoginActivity, UserProfileActivity::class.java)
                intent.putExtra(Constants.EXTRA_USER_DETAILS, user)
                startActivity(intent)
            } else {
                // Redirect the user to Main Screen after log in.
                startActivity(Intent(this@LoginActivity, DashboardActivity::class.java))
//
//            val view = View.inflate(this@LoginActivity, R.layout.dialog_info, null)
//            val builder = AlertDialog.Builder(this@LoginActivity)
//            builder.setView(view)
//            val dialog = builder.create()
//            dialog.show()
//            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
//
//            view.findViewById<Button>(R.id.btn_dialoginfoclose).setOnClickListener {
//                dialog.dismiss()
//            }
            }
            finish()
        }
    }
