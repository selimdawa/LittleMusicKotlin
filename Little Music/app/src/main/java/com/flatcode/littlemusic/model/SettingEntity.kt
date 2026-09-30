package com.flatcode.littlemusic.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "settings")
data class SettingEntity(
    @PrimaryKey var id: String = "",
    var value: String = ""
)
