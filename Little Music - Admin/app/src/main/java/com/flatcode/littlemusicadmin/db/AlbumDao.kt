package com.flatcode.littlemusicadmin.db

import androidx.room.*
import com.flatcode.littlemusicadmin.model.Album
import kotlinx.coroutines.flow.Flow

@Dao
interface AlbumDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlbum(album: Album)

    @Query("SELECT * FROM albums")
    fun getAllAlbums(): Flow<List<Album>>

    @Query("SELECT * FROM albums WHERE categoryId = :categoryId")
    fun getAlbumsByCategory(categoryId: String): Flow<List<Album>>

    @Delete
    suspend fun deleteAlbum(album: Album)
}