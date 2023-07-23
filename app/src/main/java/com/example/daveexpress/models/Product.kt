package com.example.daveexpress.models

import android.os.Parcelable
import com.example.daveexpress.utils.Constants
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
    val available_size1: String = "",
    val available_size2: String = "",
    val available_size3: String = "",
    val available_size4: String = "",
    val available_size5: String = "",
    val available_size6: String = "",
    val product_datetime: Long = 0L,
    var productId: String = "",
    val sale_price: String = "",
    val percentage_off: String = "",
    val sale_status: String = ""
): Parcelable