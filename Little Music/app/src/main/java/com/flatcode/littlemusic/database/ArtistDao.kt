package com.flatcode.littlemusic.database

import androidx.room.*
import com.flatcode.littlemusic.model.Artist

@Dao
interface ArtistDao {
    @Query("SELECT * FROM artists")
    fun getAllArtists(): List<Artist>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertArtists(artists: List<Artist>)

    @Delete
    fun deleteArtist(artist: Artist)
}