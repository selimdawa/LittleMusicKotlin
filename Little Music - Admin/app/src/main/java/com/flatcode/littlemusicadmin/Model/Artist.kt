package com.flatcode.littlemusicadmin.Model

class Artist {
    var id: String? = null
    var name: String? = null
    var image: String? = null
    var aboutTheArtist: String? = null
    var publisher: String? = null
    var interestedCount = 0
    var songsCount = 0
    var albumsCount = 0
    var timestamp: Long = 0

    constructor()

    constructor(
        id: String?, name: String?, aboutTheArtist: String?, image: String?, publisher: String?,
        timestamp: Long, interestedCount: Int, songsCount: Int, albumsCount: Int,
    ) {
        this.id = id
        this.name = name
        this.publisher = publisher
        this.aboutTheArtist = aboutTheArtist
        this.image = image
        this.timestamp = timestamp
        this.interestedCount = interestedCount
        this.songsCount = songsCount
        this.albumsCount = albumsCount
    }
}