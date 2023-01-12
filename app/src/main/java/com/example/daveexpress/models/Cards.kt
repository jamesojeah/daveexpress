package com.example.daveexpress.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class Cards (
    val user_id: String = "",
    val cardnumber: String = "",
    val cardname: String = "",
    val cvv: String = "",
    val expirydate: String = "",
    val cardtype: String = "",
    var cardId: String = ""
        ): Parcelable