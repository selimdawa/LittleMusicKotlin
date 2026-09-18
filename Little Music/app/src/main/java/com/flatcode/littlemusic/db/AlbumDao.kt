package com.flatcode.littlemusic.db

import androidx.room.*
import com.flatcode.littlemusic.model.Album

@Dao
interface AlbumDao {
    @Query("SELECT * FROM albums")
    fun getAllAlbums(): List<Album>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAlbums(albums: List<Album>)

    @Delete
    fun deleteAlbum(album: Album)
}