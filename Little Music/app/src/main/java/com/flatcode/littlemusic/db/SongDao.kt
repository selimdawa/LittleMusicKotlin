package com.flatcode.littlemusic.db

import androidx.room.*
import com.flatcode.littlemusic.model.Song

@Dao
interface SongDao {
    @Query("SELECT * FROM songs")
    fun getAllSongs(): List<Song>

    @Query("SELECT * FROM songs WHERE id = :id")
    fun getSongById(id: String): Song?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSongs(songs: List<Song>)

    @Delete
    fun deleteSong(song: Song)
}