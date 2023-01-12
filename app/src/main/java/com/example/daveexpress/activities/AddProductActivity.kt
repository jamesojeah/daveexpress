package com.example.daveexpress.activities

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.daveexpress.R
import com.example.daveexpress.databinding.ActivityAddProductBinding
import com.example.daveexpress.firestore.FirestoreClass
import com.example.daveexpress.models.Product
import com.example.daveexpress.utils.Constants
import com.example.daveexpress.utils.GlideLoader
import java.io.IOException

class AddProductActivity : BaseActivity(), View.OnClickListener {
    private lateinit var binding: ActivityAddProductBinding
    private var mSelectedImageFileURI: Uri? = null
    private var mProductImageURL: String = ""
    private var availableSizes : String = ""
    private var mSalesPrice: String = ""
    private var mPercentageOff: String = ""
    private var mProductId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()
        binding.ivAddUpdateProduct.setOnClickListener(this)
        binding.btnSubmitAddProduct.setOnClickListener(this)

        binding.rgCategory.setOnCheckedChangeListener { _, checkedId ->

            if (checkedId == R.id.rb_shoes){
                binding.tilAvailableShoesizes.visibility = View.VISIBLE
                binding.tilAvailableShirtsizes.visibility = View.INVISIBLE
            } else if (checkedId == R.id.rb_shirts){
                binding.tilAvailableShirtsizes.visibility = View.VISIBLE
                binding.tilAvailableShoesizes.visibility = View.INVISIBLE
            } else{
                binding.tilAvailableShirtsizes.visibility = View.GONE
                binding.tilAvailableShoesizes.visibility = View.GONE
            }

        }
    }

    //A function for actionBar Setup.
    private fun setupActionBar() {

        setSupportActionBar(binding.toolbarAddProductActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        binding.toolbarAddProductActivity.setNavigationOnClickListener { onBackPressed() }
    }

    val getImage = registerForActivityResult(ActivityResultContracts.GetContent(),
        ActivityResultCallback {
            mSelectedImageFileURI = it
            try{
                if (mSelectedImageFileURI != null){
                    GlideLoader(this).loadUserPicture(mSelectedImageFileURI!!, binding.ivProductImage)
                } else{

                }

            }catch (e: IOException){
                e.printStackTrace()
            }
        })


    override fun onClick(v: View?) {
        if(v != null){
            when(v.id){
                R.id.iv_add_update_product-> {
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED
                    ) {
//                        Constants.showImageChooser(this@AddProductActivity)
                        getImage.launch("image/*")
                        binding.ivAddUpdateProduct.setImageDrawable(ContextCompat.getDrawable
                            (this, R.drawable.ic_vector_edit))

                    } else {
                        /*Requests permissions to be granted to this application. These permissions
                         must be requested in your manifest, they should not be granted to your app,
                         and they should have protection level*/
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                            Constants.READ_STORAGE_PERMISSION_CODE
                        )
                    }
//                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
//                        == PackageManager.PERMISSION_GRANTED)
//                    {
//                        getImage.launch("image/*")
//                    } else {
//                        ActivityCompat.requestPermissions(
//                            this,
//                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
//                            Constants.READ_STORAGE_PERMISSION_CODE
//                        )
//                    }
                }

                R.id.btn_submit_add_product -> {
                    if (binding.etAvailableShoesizes.text.isNullOrEmpty() &&
                        binding.etAvailableShirtsizes.text.isNullOrEmpty()){
                        showErrorSnackBar(
                            resources.getString(R.string.err_msg_enter_available_sizes), true)
                    } else if (validateProductDetails()) {
//                        uploadProductDetails()
                        uploadProductImage()
                    }
                }

            }
        }
    }

    private fun uploadProductImage() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().uploadImageToCloudStorage(this, mSelectedImageFileURI, Constants.PRODUCT_IMAGE)
    }

    fun productUploadSuccess(){
        hideProgressDialog()
        Toast.makeText(
            this@AddProductActivity,
            resources.getString(R.string.product_uploaded_success_message),
            Toast.LENGTH_SHORT
        ).show()
        finish()
    }


    fun imageUploadSuccess(imageURL: String) {
//        hideProgressDialog()
        mProductImageURL = imageURL
//        updateUserProfileDetails()
        uploadProductDetails()

    }

    private fun uploadProductDetails(){
        val username = this.getSharedPreferences(Constants.DAVEEXPRESS_PREFERENCES, Context.MODE_PRIVATE)
            .getString(Constants.LOGGED_IN_USERNAME, "")!!

        val categoryType: String = when{
            binding.rbShoes.isChecked -> {
                Constants.SHOES
            }
            binding.rbShirts.isChecked -> {
                Constants.SHIRTS
            }
            binding.rbHoodies.isChecked -> {
                Constants.HOODIES
            }
            binding.rbTrousers.isChecked -> {
                Constants.TROUSERS
            }
            else -> {
                Constants.OTHERCATEGORY
            }
        }


        if (binding.etAvailableShoesizes.text.isNullOrEmpty()){
            availableSizes = binding.etAvailableShirtsizes.text.toString()
        }else{
            availableSizes = binding.etAvailableShoesizes.text.toString()
        }

        val product = Product(
            FirestoreClass().getCurrentUserID(),
            username,
            binding.etProductTitle.text.toString().trim { it <= ' ' },
            binding.etProductPrice.text.toString().trim { it <= ' ' },
            binding.etProductDescription.text.toString().trim { it <= ' ' },
            binding.etProductQuantity.text.toString().trim { it <= ' ' },
            categoryType,
            mProductImageURL,
           availableSizes,
            System.currentTimeMillis(),
            mProductId,
            mSalesPrice,
            mPercentageOff,
            Constants.NO

        )

        FirestoreClass().uploadProductDetails(this, product)
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
            //If permission is granted
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                //showErrorSnackBar("The storage permission is granted.", false)
                Constants.showImageChooser(this)
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(
                    this,
                    resources.getString(R.string.read_storage_permission_denied),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
    /**
     * A function to validate the product details.
     */
    private fun validateProductDetails(): Boolean {
        return when {

            mSelectedImageFileURI == null -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_select_product_image), true)
                false
            }

            TextUtils.isEmpty(binding.etProductTitle.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_product_title), true)
                false
            }

            TextUtils.isEmpty(binding.etProductPrice.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_product_price), true)
                false
            }

            TextUtils.isEmpty(binding.etProductDescription.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_enter_product_description),
                    true
                )
                false
            }


            TextUtils.isEmpty(binding.etProductQuantity.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_enter_product_quantity),
                    true
                )
                false

            }

            else -> {
                true
            }
        }

    }

}
