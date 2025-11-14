package com.flatcode.littlemusicadmin.Model

import com.flatcode.littlemusicadmin.Unit.DATA

class Song {
    var id: String? = null
    var publisher: String? = null
    var categoryId: String? = null
    var name: String? = null
    var artistId: String? = null
    var albumId: String? = null
    var duration: String? = null
    var songLink: String? = null
    var key: String? = null
    var viewsCount = 0
    var lovesCount = 0
    var editorsChoice = 0
    var timestamp: Long = 0

    constructor()

    constructor(
        id: String?, publisher: String?, timestamp: Long, categoryId: String?, name: String,
        albumId: String?, artistId: String?, duration: String?, songLink: String?,
        viewsCount: Int, lovesCount: Int, editorsChoice: Int,
    ) {
        var name = name
        if (name.trim { it <= ' ' } == DATA.EMPTY) {
            name = "No Name"
        }
        this.id = id
        this.publisher = publisher
        this.timestamp = timestamp
        this.categoryId = categoryId
        this.name = name
        this.artistId = artistId
        this.duration = duration
        this.albumId = albumId
        this.songLink = songLink
        this.viewsCount = viewsCount
        this.lovesCount = lovesCount
        this.editorsChoice = editorsChoice
    }
}