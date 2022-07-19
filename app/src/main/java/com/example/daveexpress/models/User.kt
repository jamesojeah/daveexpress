package com.example.daveexpress.models

import android.os.Parcelable

@kotlinx.parcelize.Parcelize
class User (
    val id: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val image: String = "",
    val mobile: Long = 0,
    val gender: String = "",
    val admin: Int = 0,
    val profileCompleted: Int = 0) :Parcelable