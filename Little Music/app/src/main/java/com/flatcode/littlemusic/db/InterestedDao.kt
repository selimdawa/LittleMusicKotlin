package com.flatcode.littlemusic.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.flatcode.littlemusic.model.Album
import com.flatcode.littlemusic.model.Artist
import com.flatcode.littlemusic.model.Category
import com.flatcode.littlemusic.model.InterestedEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface InterestedDao {

    @Query("SELECT categories.* FROM categories INNER JOIN interested ON categories.id = interested.itemId WHERE interested.userId = :userId AND interested.databaseName = :databaseName ORDER BY categories.timestamp DESC")
    fun getInterestedCategories(userId: String, databaseName: String): Flow<List<Category>>

    @Query("SELECT albums.* FROM albums INNER JOIN interested ON albums.id = interested.itemId WHERE interested.userId = :userId AND interested.databaseName = :databaseName ORDER BY albums.timestamp DESC")
    fun getInterestedAlbums(userId: String, databaseName: String): Flow<List<Album>>

    @Query("SELECT artists.* FROM artists INNER JOIN interested ON artists.id = interested.itemId WHERE interested.userId = :userId AND interested.databaseName = :databaseName ORDER BY artists.timestamp DESC")
    fun getInterestedArtists(userId: String, databaseName: String): Flow<List<Artist>>

    @Query("SELECT COUNT(*) FROM interested WHERE userId = :userId AND databaseName = :databaseName")
    fun getInterestedCount(userId: String, databaseName: String): Flow<Int>

    @Query("SELECT itemId FROM interested WHERE userId = :userId AND databaseName = :databaseName")
    fun getInterestedIds(userId: String, databaseName: String): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInterested(interested: InterestedEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInterestedList(list: List<InterestedEntity>)

    @Query("DELETE FROM interested WHERE userId = :userId AND databaseName = :databaseName AND itemId = :itemId")
    suspend fun deleteInterested(userId: String, databaseName: String, itemId: String)

    @Query("DELETE FROM interested WHERE userId = :userId AND databaseName = :databaseName")
    suspend fun deleteAllInterestedForUser(userId: String, databaseName: String)
}
