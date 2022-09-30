package com.example.daveexpress.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Order(
    val user_id: String = "",
    val items: ArrayList<CartItem> = ArrayList(),
    val address: Address = Address(),
    val title: String = "",
    val image: String = "",
    val sub_total_amount: String = "",
    val shipping_charge: String = "",
    val total_amount: String = "",
    val order_datetime: Long = 0L,
    val ordered_size: String = "",
    val order_status: String = "",
    var id: String = ""
) : Parcelable