package com.flatcode.littlemusic.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.flatcode.littlemusic.model.Song
import kotlinx.coroutines.flow.Flow

@Dao
interface SongDao {

    @Query("SELECT * FROM songs ORDER BY timestamp DESC")
    fun getAllSongs(): Flow<List<Song>>

    @Query("SELECT * FROM songs WHERE editorsChoice BETWEEN 1 AND 5 ORDER BY editorsChoice ASC")
    fun getEditorsChoiceSongs(): Flow<List<Song>>

    @Query("SELECT * FROM songs ORDER BY viewsCount DESC LIMIT :limit")
    fun getMostViewedSongs(limit: Int): Flow<List<Song>>

    @Query("SELECT * FROM songs ORDER BY lovesCount DESC LIMIT :limit")
    fun getMostLovedSongs(limit: Int): Flow<List<Song>>

    @Query("SELECT * FROM songs ORDER BY timestamp DESC LIMIT :limit")
    fun getLatestSongs(limit: Int): Flow<List<Song>>

    @Query("SELECT * FROM songs WHERE categoryId = :categoryId ORDER BY timestamp DESC")
    fun getSongsByCategory(categoryId: String): Flow<List<Song>>

    @Query("SELECT * FROM songs WHERE albumId = :albumId ORDER BY timestamp DESC")
    fun getSongsByAlbum(albumId: String): Flow<List<Song>>

    @Query("SELECT * FROM songs WHERE artistId = :artistId ORDER BY timestamp DESC")
    fun getSongsByArtist(artistId: String): Flow<List<Song>>

    @Query("SELECT * FROM songs WHERE id = :id")
    fun getSongById(id: String): Flow<Song?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSongs(songs: List<Song>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSong(song: Song)

    @Delete
    suspend fun deleteSong(song: Song)
}