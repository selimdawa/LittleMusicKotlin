package com.flatcode.littlemusicadmin.db

import androidx.room.*
import com.flatcode.littlemusicadmin.model.Artist
import kotlinx.coroutines.flow.Flow

@Dao
interface ArtistDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArtist(artist: Artist)

    @Query("SELECT * FROM artists")
    fun getAllArtists(): Flow<List<Artist>>

    @Query("SELECT * FROM artists WHERE id = :id")
    suspend fun getArtistById(id: String): Artist?

    @Delete
    suspend fun deleteArtist(artist: Artist)
}