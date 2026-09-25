package com.flatcode.littlemusicadmin.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    var id: String = "",
    var username: String? = null,
    var profileImage: String? = null,
    var email: String? = null,
    var timestamp: Long = 0,
    var version: Int = 0
) : Parcelable
