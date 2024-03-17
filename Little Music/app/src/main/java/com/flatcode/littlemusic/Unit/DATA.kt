package com.flatcode.littlemusic.Unitimport

import com.google.firebase.auth.FirebaseAuth

object DATA {
    //Database
    var USERS = "Users"
    var TOOLS = "Tools"
    var EMAIL = "email"
    var CATEGORIES = "Categories"
    var ALBUMS = "Albums"
    var ARTISTS = "Artists"
    var ALBUM = "Album"
    var ARTIST = "Artist"
    var SONGS = "Songs"
    var INTERESTED = "Interested"
    var LOVES = "Loves"
    var VERSION = "version"
    var PRIVACY_POLICY = "privacyPolicy"
    var BASIC = "basic"
    var USER_NAME = "username"
    var PROFILE_IMAGE = "profileImage"
    var TIMESTAMP = "timestamp"
    var ID = "id"
    var IMAGE = "image"
    var SLIDER_SHOW = "SliderShow"
    var CATEGORY = "category"
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
    var CURRENT_VERSION = 1

    //Shared
    var PROFILE_ID = "profileId"
    var COLOR_OPTION = "color_option"
    var CATEGORY_ID = "categoryId"
    var ARTIST_ID = "artistId"
    var ARTIST_ABOUT = "artistAbout"
    var ALBUM_ID = "albumId"
    var SHOW_MORE_TYPE = "showMoreType"
    var CATEGORY_NAME = "categoryName"
    var ALBUM_NAME = "albumName"
    var ALBUM_IMAGE = "albumImage"
    var ARTIST_NAME = "artistName"
    var ARTIST_IMAGE = "artistImage"
    var SHOW_MORE_NAME = "showMoreName"
    var SHOW_MORE_BOOLEAN = "showMoreBoolean"

    //Other
    var EMPTY = ""
    var SPACE = " "
    var MIX_SQUARE = 500
    var ZERO = 0
    var ORDER_MAIN = 5 // Here Max Item Show
    var searchStatus = false
    var FB_ID = ""
    var WEB_SITE = ""
    val AUTH = FirebaseAuth.getInstance()
    val FIREBASE_USER = AUTH.currentUser
    val FirebaseUserUid = FIREBASE_USER!!.uid
}