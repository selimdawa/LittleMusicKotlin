package com.flatcode.littlemusicadmin.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.flatcode.littlemusicadmin.model.*

@Database(
    entities = [
        User::class,
        Category::class,
        Artist::class,
        Album::class,
        Song::class
    ],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun categoryDao(): CategoryDao
    abstract fun artistDao(): ArtistDao
    abstract fun albumDao(): AlbumDao
    abstract fun songDao(): SongDao
}