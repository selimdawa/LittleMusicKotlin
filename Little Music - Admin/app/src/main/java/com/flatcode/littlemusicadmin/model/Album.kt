package com.flatcode.littlemusicadmin.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Album(
    var id: String = "",
    var name: String? = null,
    var image: String? = null,
    var artistId: String? = null,
    var categoryId: String? = null,


    var interestedCount: Int = 0,
    var songsCount: Int = 0,
    var timestamp: Long = 0
) : Parcelable
