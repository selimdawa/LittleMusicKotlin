package com.flatcode.littlemusic.di

import android.content.Context
import androidx.room.Room
import com.flatcode.littlemusic.db.AlbumDao
import com.flatcode.littlemusic.db.AppDatabase
import com.flatcode.littlemusic.db.ArtistDao
import com.flatcode.littlemusic.db.CategoryDao
import com.flatcode.littlemusic.db.FavoriteDao
import com.flatcode.littlemusic.db.InterestedDao
import com.flatcode.littlemusic.db.SettingDao
import com.flatcode.littlemusic.db.SliderDao
import com.flatcode.littlemusic.db.SongDao
import com.flatcode.littlemusic.db.UserDao
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
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "little_music_database"
        ).fallbackToDestructiveMigration(true).build()
    }

    @Provides
    fun provideSongDao(database: AppDatabase): SongDao = database.songDao()

    @Provides
    fun provideCategoryDao(database: AppDatabase): CategoryDao = database.categoryDao()

    @Provides
    fun provideUserDao(database: AppDatabase): UserDao = database.userDao()

    @Provides
    fun provideAlbumDao(database: AppDatabase): AlbumDao = database.albumDao()

    @Provides
    fun provideArtistDao(database: AppDatabase): ArtistDao = database.artistDao()

    @Provides
    fun provideFavoriteDao(database: AppDatabase): FavoriteDao = database.favoriteDao()

    @Provides
    fun provideInterestedDao(database: AppDatabase): InterestedDao = database.interestedDao()

    @Provides
    fun provideSliderDao(database: AppDatabase): SliderDao = database.sliderDao()

    @Provides
    fun provideSettingDao(database: AppDatabase): SettingDao = database.settingDao()
}
