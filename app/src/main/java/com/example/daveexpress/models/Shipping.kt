package com.example.daveexpress.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Shipping(
    val lagosfee: String = "",
    val deltafee: String = "",
    val abujafee: String = "",
    val bayelsafee: String = "",
    val enugufee: String = "",
    val edofee: String = "",
    val anambrafee: String = "",
    val othersfee: String = "",
    var id: String = ""
): Parcelable