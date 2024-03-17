package com.flatcode.littlemusicadmin.Model

class Album {
    var id: String? = null
    var name: String? = null
    var image: String? = null
    var artistId: String? = null
    var categoryId: String? = null
    var publisher: String? = null
    var interestedCount = 0
    var songsCount = 0
    var timestamp: Long = 0

    constructor()

    constructor(
        id: String?, name: String?, artistId: String?, categoryId: String?, image: String?,
        publisher: String?, timestamp: Long, interestedCount: Int, songsCount: Int,
    ) {
        this.id = id
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