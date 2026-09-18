package com.flatcode.littlemusicadmin.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "artists")
data class Artist(
    @PrimaryKey
    var id: String = "",
    var name: String? = null,
    var image: String? = null,
    var aboutTheArtist: String? = null,
    var publisher: String? = null,
    var interestedCount: Int = 0,
    var songsCount: Int = 0,
    var albumsCount: Int = 0,
    var timestamp: Long = 0
) : Parcelable {
    constructor() : this("", null, null, null, null, 0, 0, 0, 0)
}
