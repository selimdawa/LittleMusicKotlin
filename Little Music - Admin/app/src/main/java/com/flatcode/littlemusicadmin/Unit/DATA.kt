package com.flatcode.littlemusicadmin.Unit

import com.google.firebase.auth.FirebaseAuth

object DATA {
    //Database
    var USERS = "Users"
    var TOOLS = "Tools"
    var CATEGORIES = "Categories"
    var ALBUMS = "Albums"
    var ARTISTS = "Artists"
    var ALBUM = "Album"
    var ARTIST = "Artist"
    var SONGS = "Songs"
    var SONG = "Song"
    var INTERESTED = "Interested"
    var LOVES = "Loves"
    var PRIVACY_POLICY = "privacyPolicy"
    var BASIC = "basic"
    var USER_NAME = "username"
    var PROFILE_IMAGE = "profileImage"
    var TIMESTAMP = "timestamp"
    var ID = "id"
    var IMAGE = "image"
    var SLIDER_SHOW = "SliderShow"
    var PUBLISHER = "publisher"
    var CATEGORY = "category"
    var ABOUT_THE_ARTIST = "aboutTheArtist"
    var NULL = "null"
    var FAVORITES = "Favorites"
    var VIEWS_COUNT = "viewsCount"
    var INTERESTED_COUNT = "interestedCount"
    var ALBUMS_COUNT = "albumsCount"
    var SONGS_COUNT = "songsCount"
    var LOVES_COUNT = "lovesCount"
    var EDITORS_CHOICE = "editorsChoice"
    var NAME = "name"
    var DOT = "."

    //Shared
    var PROFILE_ID = "profileId"
    var COLOR_OPTION = "color_option"
    var EDITORS_CHOICE_ID = "editorsChoiceId"
    var CATEGORY_ID = "categoryId"
    var ARTIST_ID = "artistId"
    var ALBUM_ID = "albumId"
    var SONG_ID = "songId"
    var DURATION = "duration"
    var SONG_LINK = "songLink"
    var OLD_ID = "oldId"
    var CATEGORY_NAME = "categoryName"
    var ALBUM_NAME = "albumName"
    var ALBUM_IMAGE = "albumImage"
    var ARTIST_NAME = "artistName"
    var ARTIST_IMAGE = "artistImage"
    var ARTIST_ABOUT = "artistAbout"

    //Other
    var EMPTY = ""
    var SPACE = " "
    var MIX_SQUARE = 500
    var MIX_SLIDER_X = 680
    var MIX_SLIDER_Y = 360
    var ZERO = 0
    var searchStatus = false
    var isChange = false
    val AUTH = FirebaseAuth.getInstance()
    val FIREBASE_USER = AUTH.currentUser
    val FirebaseUserUid = FIREBASE_USER!!.uid
}