package com.flatcode.littlemusicadmin.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "albums")
data class Album(
    @PrimaryKey var id: String = "",
    var name: String? = null,
    var image: String? = null,
    var artistId: String? = null,
    var categoryId: String? = null,
    var publisher: String? = null,
    var interestedCount: Int = 0,
    var songsCount: Int = 0,
    var timestamp: Long = 0
) : Parcelable