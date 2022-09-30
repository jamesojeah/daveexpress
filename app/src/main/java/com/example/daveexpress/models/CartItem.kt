package com.example.daveexpress.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CartItem (
        val user_id: String = "",
        val product_owner_id: String = "",
        val product_id: String = "",
        val title: String = "",
        val price: String = "",
        val image: String = "",
        var cart_quantity: String = "",
        var stock_quantity: String = "",
        var chosen_size: String = "",
        var sale_price: String = "",
        var sale_status: String = "",
        var id: String = ""
        ): Parcelable