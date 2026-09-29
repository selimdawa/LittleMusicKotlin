package com.flatcode.littlemusic.model

import androidx.room.Entity

@Entity(tableName = "favorites", primaryKeys = ["userId", "songId"])
data class FavoriteEntity(
    val userId: String = "",
    val songId: String = ""
)
