package com.flatcode.littlemusic.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.flatcode.littlemusic.model.FavoriteEntity
import com.flatcode.littlemusic.model.Song
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {

    @Query("SELECT songs.* FROM songs INNER JOIN favorites ON songs.id = favorites.songId WHERE favorites.userId = :userId ORDER BY songs.timestamp DESC")
    fun getFavoriteSongs(userId: String): Flow<List<Song>>

    @Query("SELECT COUNT(songs.id) FROM songs INNER JOIN favorites ON songs.id = favorites.songId WHERE favorites.userId = :userId")
    fun getFavoriteCount(userId: String): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorites(favorites: List<FavoriteEntity>)

    @Query("DELETE FROM favorites WHERE userId = :userId AND songId = :songId")
    suspend fun deleteFavorite(userId: String, songId: String)

    @Query("DELETE FROM favorites WHERE userId = :userId")
    suspend fun deleteAllFavoritesForUser(userId: String)
}
