package com.flatcode.littlemusic.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.flatcode.littlemusic.model.*

@Database(
    entities = [Song::class, Category::class, User::class, Album::class, Artist::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun songDao(): SongDao
    abstract fun categoryDao(): CategoryDao
    abstract fun userDao(): UserDao
    abstract fun albumDao(): AlbumDao
    abstract fun artistDao(): ArtistDao
}