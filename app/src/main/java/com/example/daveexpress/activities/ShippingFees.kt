package com.example.daveexpress.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.example.daveexpress.R
import com.example.daveexpress.databinding.ActivitySettingsBinding
import com.example.daveexpress.databinding.ActivityShippingFeesBinding
import com.example.daveexpress.firestore.FirestoreClass
import com.example.daveexpress.models.Shipping
import com.example.daveexpress.utils.Constants
import com.google.firestore.v1.FirestoreGrpc.FirestoreStub

class ShippingFees : BaseActivity() {

    private var mShippingId: String = ""
    private lateinit var mShippingFees: Shipping

    private lateinit var binding: ActivityShippingFeesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShippingFeesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar()

        getshippingfees()


        binding.saveShippingFee.setOnClickListener {

            if (validateshippingfees()) {
                savetheShippingFee()
            }
        }

        binding.updateShippingFee.setOnClickListener {
            if (validateshippingfees()){
                updatetheShippingFee()
            }
        }

    }

    private fun getshippingfees() {
        mShippingId = Constants.SHIPPINGFEEID
        FirestoreClass().getshippingfees(this, mShippingId)
    }

    private fun updatetheShippingFee() {

        val lagosfee: String = binding.lagoset.text.toString().trim { it <= ' ' }
        val deltafee: String = binding.deltaet.text.toString().trim { it <= ' ' }
        val abujafee: String = binding.abujaet.text.toString().trim { it <= ' ' }
        val bayelsafee: String = binding.bayelsaet.text.toString().trim { it <= ' ' }
        val enugufee: String = binding.Enuguet.text.toString().trim { it <= ' ' }
        val edofee: String = binding.edoet.text.toString().trim { it <= ' ' }
        val anambrafee: String = binding.anambraet.text.toString().trim { it <= ' ' }
        val othersfee: String = binding.otherset.text.toString().trim { it <= ' ' }


        val shippingfeeHashmap = HashMap<String, Any>()

        if (lagosfee != mShippingFees.lagosfee){
            shippingfeeHashmap[Constants.LAGOS_FEE] = lagosfee
        }

        if (deltafee != mShippingFees.deltafee){
            shippingfeeHashmap[Constants.DELTA_FEE] = deltafee
        }

        if (abujafee != mShippingFees.abujafee){
            shippingfeeHashmap[Constants.ABUJA_FEE] = abujafee
        }

        if (bayelsafee != mShippingFees.bayelsafee){
            shippingfeeHashmap[Constants.BAYELSA_FEE] = bayelsafee
        }

        if (enugufee != mShippingFees.enugufee){
            shippingfeeHashmap[Constants.ENUGU_FEE] = enugufee
        }

        if (edofee != mShippingFees.edofee){
            shippingfeeHashmap[Constants.EDO_FEE] = edofee
        }

        if (anambrafee != mShippingFees.anambrafee){
            shippingfeeHashmap[Constants.ANAMBRA_FEE] = anambrafee
        }

        if (othersfee != mShippingFees.othersfee){
            shippingfeeHashmap[Constants.OTHERS_FEE] = othersfee
        }

            FirestoreClass().updateshippingfees(this@ShippingFees, mShippingFees.id, shippingfeeHashmap)

    }


    private fun savetheShippingFee() {

        val lagosfee: String = binding.lagoset.text.toString().trim { it <= ' ' }
        val deltafee: String = binding.deltaet.text.toString().trim { it <= ' ' }
        val abujafee: String = binding.abujaet.text.toString().trim { it <= ' ' }
        val bayelsafee: String = binding.bayelsaet.text.toString().trim { it <= ' ' }
        val enugufee: String = binding.Enuguet.text.toString().trim { it <= ' ' }
        val edofee: String = binding.edoet.text.toString().trim { it <= ' ' }
        val anambrafee: String = binding.anambraet.text.toString().trim { it <= ' ' }
        val othersfee: String = binding.otherset.text.toString().trim { it <= ' ' }

        val shippingfeesmodel = Shipping(
            lagosfee,
            deltafee,
            abujafee,
            bayelsafee,
            enugufee,
            edofee,
            anambrafee,
            othersfee
        )

        FirestoreClass().addShippingFees(this@ShippingFees, shippingfeesmodel)

    }

    fun shippingfeesUpdateSuccess(){
        Toast.makeText(
            this@ShippingFees,
            resources.getString(R.string.shippingfee_updated_success_message),
            Toast.LENGTH_SHORT
        ).show()
    }

     fun addshippingfeesuccess(){
         Toast.makeText(
             this@ShippingFees,
             resources.getString(R.string.shippingfee_uploaded_success_message),
             Toast.LENGTH_SHORT
         ).show()
    }

     fun getshippingfeesSuccess(shipping: Shipping){


        mShippingFees  = shipping



         binding.abujashippingfee.text = "₦${shipping.abujafee}"
         binding.anambrashippingfee.text = "₦${shipping.anambrafee}"
         binding.bayelsashippingfee.text = "₦${shipping.bayelsafee}"
         binding.deltashippingfee.text = "₦${shipping.deltafee}"
         binding.edoshippingfee.text = "₦${shipping.edofee}"
         binding.enugushippingfee.text = "₦${shipping.enugufee}"
         binding.lagosshippingfee.text = "₦${shipping.lagosfee}"
         binding.othersshippingfee.text = "₦${shipping.othersfee}"

        binding.saveShippingFee.visibility = View.GONE

     }

    private fun setupActionBar() {

        setSupportActionBar(binding.toolbarShippingFees)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        binding.toolbarShippingFees.setNavigationOnClickListener { onBackPressed() }
    }

    private fun validateshippingfees(): Boolean{
        return when {
            TextUtils.isEmpty(binding.lagoset.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_shipping_lagos),
                    true)
                false
            }
            TextUtils.isEmpty(binding.deltaet.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_shipping_delta),
                    true)
                false
            }
            TextUtils.isEmpty(binding.abujaet.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_shipping_abuja),
                    true)
                false
            }
            TextUtils.isEmpty(binding.bayelsaet.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_shipping_bayelsa),
                    true)
                false
            }
            TextUtils.isEmpty(binding.Enuguet.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_shipping_enugu),
                    true)
                false
            }
            TextUtils.isEmpty(binding.edoet.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_shipping_edo),
                    true)
                false
            }
            TextUtils.isEmpty(binding.anambraet.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_shipping_anambra),
                    true)
                false
            }
            TextUtils.isEmpty(binding.otherset.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_shipping_others),
                    true)
                false
            }
            else -> {
                true
            }
        }
    }
}