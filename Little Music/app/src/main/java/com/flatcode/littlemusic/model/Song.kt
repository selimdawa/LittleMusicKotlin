package com.flatcode.littlemusic.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
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
    var timestamp: Long = 0,
    @Ignore var localPath: String? = null,
    @Ignore var categoryName: String? = null,
    @Ignore var albumName: String? = null,
    @Ignore var artistName: String? = null
) : Parcelable {
    constructor() : this(
        "", null, null, null, null, null, null, null, null,
        0, 0, 0, 0, null, null, null, null
    )
}