package com.flatcode.littlemusic.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.flatcode.littlemusic.model.Album
import com.flatcode.littlemusic.model.Artist
import com.flatcode.littlemusic.model.Category
import com.flatcode.littlemusic.model.FavoriteEntity
import com.flatcode.littlemusic.model.InterestedEntity
import com.flatcode.littlemusic.model.SliderEntity
import com.flatcode.littlemusic.model.Song
import com.flatcode.littlemusic.model.User

@Database(
    entities = [
        Song::class, Category::class, User::class, Album::class, Artist::class,
        FavoriteEntity::class, InterestedEntity::class, SliderEntity::class
    ],
    version = 2,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun songDao(): SongDao
    abstract fun categoryDao(): CategoryDao
    abstract fun userDao(): UserDao
    abstract fun albumDao(): AlbumDao
    abstract fun artistDao(): ArtistDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun interestedDao(): InterestedDao
    abstract fun sliderDao(): SliderDao
}
