package com.flatcode.littlemusicadmin.di

import android.content.Context
import androidx.room.Room
import com.flatcode.littlemusicadmin.db.AlbumDao
import com.flatcode.littlemusicadmin.db.AppDatabase
import com.flatcode.littlemusicadmin.db.ArtistDao
import com.flatcode.littlemusicadmin.db.CategoryDao
import com.flatcode.littlemusicadmin.db.SongDao
import com.flatcode.littlemusicadmin.db.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "little_music_db"
        ).build()
    }

    @Provides
    fun provideUserDao(db: AppDatabase): UserDao = db.userDao()

    @Provides
    fun provideCategoryDao(db: AppDatabase): CategoryDao = db.categoryDao()

    @Provides
    fun provideArtistDao(db: AppDatabase): ArtistDao = db.artistDao()

    @Provides
    fun provideAlbumDao(db: AppDatabase): AlbumDao = db.albumDao()

    @Provides
    fun provideSongDao(db: AppDatabase): SongDao = db.songDao()
}
