package com.flatcode.littlemusicadmin.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "songs")
data class Song(
    @PrimaryKey var id: String = "",
    var publisher: String? = null,
    var categoryId: String? = null,
    var name: String? = null,
    var artistId: String? = null,
    var albumId: String? = null,
    var duration: String? = null,
    var songLink: String? = null,
    var key: String? = null,
    var viewsCount: Int = 0,
    var lovesCount: Int = 0,
    var editorsChoice: Int = 0,
    var timestamp: Long = 0
) : Parcelable