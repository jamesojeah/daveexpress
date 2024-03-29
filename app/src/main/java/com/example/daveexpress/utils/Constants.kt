package com.example.daveexpress.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap

object Constants {
    const val PSTK_PUBLIC_KEY: String = "pk_test_ed24693280de878e2e0cfcb8663d9324466a1ea6"
    const val USERS: String = "users"
    const val DAVEEXPRESS_PREFERENCES: String = "DaveExpressPrefs"
    const val LOGGED_IN_USERNAME: String = "logged_in_username"
    const val EXTRA_USER_DETAILS: String = "extra_user_details"
    const val READ_STORAGE_PERMISSION_CODE = 2
    const val PICK_IMAGE_REQUEST_CODE = 1
    const val EXTRA_PRODUCT_OWNER_ID: String = "extra_product_owner_id"
    const val DEFAULT_CART_QUANTITY: String = "1"
    const val CART_ITEMS: String = "cart_items"
    const val PRODUCT_ID: String = "product_id"
    const val CART_QUANTITY: String = "cart_quantity"
    const val FIRST_NAME: String = "firstName"
    const val LAST_NAME: String = "lastName"
    const val MALE: String = "male"
    const val FEMALE: String = "female"
    const val SHIPPINGFEES: String = "shippingfees"
    const val MOBILE: String = "mobile"
    const val GENDER: String = "gender"
    const val IMAGE: String = "image"
    const val IS_AN_ADMIN = "admin"
    const val COMPLETE_PROFILE: String = "profileCompleted"
    const val USER_PROFILE_IMAGE: String = "User_Profile_Image"
    const val PRODUCT_IMAGE: String = "Product_Image"
    const val PRODUCTS: String = "products"
    const val USER_ID: String = "user_id"
    const val CARDS: String = "cards"

    const val LAGOS_FEE: String = "lagosFee"
    const val DELTA_FEE: String = "deltaFee"
    const val ABUJA_FEE: String = "abujaFee"
    const val BAYELSA_FEE: String = "bayelsaFee"
    const val ENUGU_FEE: String = "enuguFee"
    const val EDO_FEE: String = "edoFee"
    const val ANAMBRA_FEE: String = "anambraFee"
    const val OTHERS_FEE: String = "othersFee"
    const val SHIPPINGFEEID: String = "ShippingfeeId"



    const val SHOES: String = "Shoes"
    const val SHIRTS: String = "Shirts"
    const val TROUSERS: String = "Trousers"
    const val HOODIES: String = "Hoodies"
    const val OTHERCATEGORY: String = "Other_category"

    const val EXTRA_PRODUCT_ID: String = "extra_product_id"

    const val EXTRA_ADDRESS_DETAILS: String = "AddressDetails"

    const val EXTRA_SELECT_ADDRESS: String = "extra_select_address"
    const val EXTRA_SELECTED_ADDRESS: String = "extra_selected_address"
    // Declare a global constant variable to notify the add address.
    const val ADD_ADDRESS_REQUEST_CODE: Int = 121

    const val HOME: String = "Home"
    const val OFFICE: String = "Office"
    const val OTHER: String = "Other"

    const val ORDERS: String = "orders"

    const val ORDER_STATUS: String = "order_status"

    const val SALES_PRICE: String = "sale_price"
    const val PERCENTAGE_OFF: String = "percentage_off"

    const val SALE_STATUS: String = "sale_status"
    const val YES: String = "yes"
    const val NO: String = "no"

    const val PENDING: String = "Pending"
    const val IN_PROCESS: String = "In Process"
    const val DELIVERED: String = "Delivered"

    const val ONESIGNAL_APP_ID = "db181adb-d09a-48d0-8ad1-484bf1d49fd2"

    const val STOCK_QUANTITY: String = "stock_quantity"
    const val EXTRA_SOLD_PRODUCT_DETAILS: String = "extra_sold_product_details"
    const val EXTRA_OUT_OF_STOCK: String = "extra_out_of_stock"

    const val EXTRA_MY_ORDER_DETAILS: String = "extra_MY_ORDER_DETAILS"
    const val SOLD_PRODUCTS: String = "sold_products"
    const val ADDRESSES: String = "addresses"
    /**
     * A function for user profile image selection from phone storage.
     */
    fun showImageChooser(activity: Activity) {
        // An intent for launching the image selection of phone storage.
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        // Launches the image selection of phone storage using the constant code.
        activity.startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }

    /**
     * A function to get the image file extension of the selected image.
     *
     * @param activity Activity reference.
     * @param uri Image file uri.
     */
    fun getFileExtension(activity: Activity, uri: Uri?): String? {
        /*
         * MimeTypeMap: Two-way map that maps MIME-types to file extensions and vice versa.
         *
         * getSingleton(): Get the singleton instance of MimeTypeMap.
         *
         * getExtensionFromMimeType: Return the registered extension for the given MIME type.
         *
         * contentResolver.getType: Return the MIME type of the given content URL.
         */
        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }
}