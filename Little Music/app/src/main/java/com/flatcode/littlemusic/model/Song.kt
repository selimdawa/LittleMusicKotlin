package com.flatcode.littlemusic.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.flatcode.littlemusic.utils.DATA
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "songs")
class Song(
    @PrimaryKey
    var id: String = "",
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
) : Parcelable {

    constructor(
        id: String?, publisher: String?, timestamp: Long, categoryId: String?, name: String,
        albumId: String?, artistId: String?, duration: String?, songLink: String?,
        viewsCount: Int, lovesCount: Int, editorsChoice: Int
    ) : this() {
        var finalName = name
        if (finalName.trim { it <= ' ' } == DATA.EMPTY) {
            finalName = "No Name"
        }
        this.id = id ?: ""
        this.publisher = publisher
        this.timestamp = timestamp
        this.categoryId = categoryId
        this.name = finalName
        this.artistId = artistId
        this.duration = duration
        this.albumId = albumId
        this.songLink = songLink
        this.viewsCount = viewsCount
        this.lovesCount = lovesCount
        this.editorsChoice = editorsChoice
    }
}