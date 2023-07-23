package com.example.daveexpress.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize

data class OS(
    val user_id: String = "",
    var productId: String = "",
    val title: String = "",
    val price: String = "",
    val image: String = "",
    val quantity: String = "",
    val sold_size: String = "",
    var id: String = ""
)  : Parcelable