package com.example.daveexpress.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Product(
    val user_id: String = "",
    val user_name: String = "",
    val title: String = "",
    val price: String = "",
    val description: String = "",
    val stock_quantity: String = "",
    val category: String ="",
    val image: String = "",
    val available_sizes: String = "",
    val product_datetime: Long = 0L,
    var product_id: String = "",
): Parcelable