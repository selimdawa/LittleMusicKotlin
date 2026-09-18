package com.flatcode.littlemusic.utils

import com.flatcode.littlemusic.ui.main.*
import com.flatcode.littlemusic.ui.auth.*
import com.flatcode.littlemusic.ui.category.*
import com.flatcode.littlemusic.ui.album.*
import com.flatcode.littlemusic.ui.artist.*
import com.flatcode.littlemusic.ui.profile.*
import com.flatcode.littlemusic.ui.favorites.*
import com.flatcode.littlemusic.ui.settings.*
import com.flatcode.littlemusic.ui.showmore.*

object CLASS {
    var MAIN: Class<*> = MainActivity::class.java
    var SPLASH: Class<*> = SplashActivity::class.java
    var AUTH: Class<*> = AuthActivity::class.java
    var LOGIN: Class<*> = LoginActivity::class.java
    var REGISTER: Class<*> = RegisterActivity::class.java
    var FORGET_PASSWORD: Class<*> = ForgetPasswordActivity::class.java
    var CATEGORY_SONGS: Class<*> = CategorySongsActivity::class.java
    var CATEGORIES: Class<*> = CategoriesActivity::class.java
    var ALBUMS: Class<*> = AlbumsActivity::class.java
    var ALBUM_SONGS: Class<*> = AlbumSongsActivity::class.java
    var ARTISTS: Class<*> = ArtistsActivity::class.java
    var ARTIST_SONGS: Class<*> = ArtistSongsActivity::class.java
    var PROFILE: Class<*> = ProfileActivity::class.java
    var PROFILE_EDIT: Class<*> = ProfileEditActivity::class.java
    var FAVORITES: Class<*> = FavoritesActivity::class.java
    var PRIVACY_POLICY: Class<*> = PrivacyPolicyActivity::class.java
    var SHOW_MORE: Class<*> = ShowMoreActivity::class.java
    var MY_ARTISTS: Class<*> = MyArtistsActivity::class.java
    var MY_ALBUMS: Class<*> = MyAlbumsActivity::class.java
    var MY_CATEGORIES: Class<*> = MyCategoriesActivity::class.java
}