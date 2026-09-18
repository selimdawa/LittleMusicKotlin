package com.flatcode.littlemusicadmin.db

import androidx.room.*
import com.flatcode.littlemusicadmin.model.Song
import kotlinx.coroutines.flow.Flow

@Dao
interface SongDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSong(song: Song)

    @Query("SELECT * FROM songs")
    fun getAllSongs(): Flow<List<Song>>

    @Query("SELECT * FROM songs WHERE albumId = :albumId")
    fun getSongsByAlbum(albumId: String): Flow<List<Song>>

    @Query("SELECT * FROM songs WHERE artistId = :artistId")
    fun getSongsByArtist(artistId: String): Flow<List<Song>>

    @Delete
    suspend fun deleteSong(song: Song)
}