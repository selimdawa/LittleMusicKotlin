package com.flatcode.littlemusic.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "albums")
class Album(
    @PrimaryKey
    var id: String = "",
    var name: String? = null,
    var image: String? = null,
    var artistId: String? = null,
    var categoryId: String? = null,
    var publisher: String? = null,
    var interestedCount: Int = 0,
    var songsCount: Int = 0,
    var timestamp: Long = 0
) : Parcelable {

    constructor(
        id: String?, name: String?, artistId: String?, categoryId: String?, image: String?,
        publisher: String?, timestamp: Long, interestedCount: Int, songsCount: Int
    ) : this() {
        this.id = id ?: ""
        this.name = name
        this.publisher = publisher
        this.artistId = artistId
        this.categoryId = categoryId
        this.image = image
        this.timestamp = timestamp
        this.interestedCount = interestedCount
        this.songsCount = songsCount
    }
}