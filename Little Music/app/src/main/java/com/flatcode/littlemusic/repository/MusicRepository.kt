package com.flatcode.littlemusic.repository

import com.flatcode.littlemusic.db.AlbumDao
import com.flatcode.littlemusic.db.ArtistDao
import com.flatcode.littlemusic.db.CategoryDao
import com.flatcode.littlemusic.db.FavoriteDao
import com.flatcode.littlemusic.db.InterestedDao
import com.flatcode.littlemusic.db.SongDao
import com.flatcode.littlemusic.model.Album
import com.flatcode.littlemusic.model.Artist
import com.flatcode.littlemusic.model.Category
import com.flatcode.littlemusic.model.FavoriteEntity
import com.flatcode.littlemusic.model.InterestedEntity
import com.flatcode.littlemusic.model.Song
import com.flatcode.littlemusic.utils.DATA
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicRepository @Inject constructor(
    private val songDao: SongDao,
    private val categoryDao: CategoryDao,
    private val albumDao: AlbumDao,
    private val artistDao: ArtistDao,
    private val favoriteDao: FavoriteDao,
    private val interestedDao: InterestedDao
) {

    private val database = FirebaseDatabase.getInstance()

    fun getCategories(): Flow<List<Category>> {
        syncCategories()
        return categoryDao.getAllCategories()
    }

    fun getSongs(orderBy: String, limit: Int? = null): Flow<List<Song>> {
        syncSongs(orderBy, limit)
        val songsFlow = when (orderBy) {
            DATA.EDITORS_CHOICE -> songDao.getEditorsChoiceSongs()
            DATA.VIEWS_COUNT -> songDao.getMostViewedSongs(limit ?: 100)
            DATA.LOVES_COUNT -> songDao.getMostLovedSongs(limit ?: 100)
            else -> songDao.getLatestSongs(limit ?: 100)
        }
        return enrichSongsFlow(songsFlow)
    }

    fun getSongsByCategory(categoryId: String): Flow<List<Song>> {
        syncSongs(DATA.TIMESTAMP, null)
        return enrichSongsFlow(songDao.getSongsByCategory(categoryId))
    }

    fun getSongsByAlbum(albumId: String): Flow<List<Song>> {
        syncSongs(DATA.TIMESTAMP, null)
        return enrichSongsFlow(songDao.getSongsByAlbum(albumId))
    }

    fun getSongsByArtist(artistId: String): Flow<List<Song>> {
        syncSongs(DATA.TIMESTAMP, null)
        return enrichSongsFlow(songDao.getSongsByArtist(artistId))
    }

    fun getFavoriteSongs(userId: String): Flow<List<Song>> {
        syncFavorites(userId)
        return enrichSongsFlow(favoriteDao.getFavoriteSongs(userId))
    }

    private fun enrichSongsFlow(songsFlow: Flow<List<Song>>): Flow<List<Song>> {
        return combine(
            songsFlow,
            categoryDao.getAllCategories(),
            albumDao.getAllAlbums(),
            artistDao.getAllArtists()
        ) { songs, categories, albums, artists ->
            val catMap = categories.associateBy { it.id }
            val albumMap = albums.associateBy { it.id }
            val artistMap = artists.associateBy { it.id }

            songs.map { song ->
                song.categoryName = catMap[song.categoryId]?.name ?: song.categoryName
                song.albumName = albumMap[song.albumId]?.name ?: song.albumName
                song.artistName = artistMap[song.artistId]?.name ?: song.artistName
                song
            }
        }
    }

    fun getAlbums(orderBy: String = DATA.TIMESTAMP): Flow<List<Album>> {
        syncAlbums(orderBy)
        return albumDao.getAllAlbums()
    }

    fun getArtists(orderBy: String = DATA.TIMESTAMP): Flow<List<Artist>> {
        syncArtists(orderBy)
        return artistDao.getAllArtists()
    }

    fun getInterestedCategories(userId: String): Flow<List<Category>> {
        syncCategories()
        syncInterested(userId, DATA.CATEGORIES)
        return interestedDao.getInterestedCategories(userId, DATA.CATEGORIES)
    }

    fun getInterestedAlbums(userId: String): Flow<List<Album>> {
        syncAlbums()
        syncInterested(userId, DATA.ALBUMS)
        return interestedDao.getInterestedAlbums(userId, DATA.ALBUMS)
    }

    fun getInterestedArtists(userId: String): Flow<List<Artist>> {
        syncArtists()
        syncInterested(userId, DATA.ARTISTS)
        return interestedDao.getInterestedArtists(userId, DATA.ARTISTS)
    }

    fun getFavoriteCount(userId: String): Flow<Int> {
        syncFavorites(userId)
        return favoriteDao.getFavoriteCount(userId)
    }

    fun getInterestedCount(userId: String, databaseName: String): Flow<Int> {
        syncInterested(userId, databaseName)
        return interestedDao.getInterestedCount(userId, databaseName)
    }

    fun getInterestedIds(userId: String, databaseName: String): Flow<List<String>> {
        syncInterested(userId, databaseName)
        return interestedDao.getInterestedIds(userId, databaseName)
    }

    private fun syncCategories() {
        database.getReference(DATA.CATEGORIES)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = mutableListOf<Category>()
                    for (child in snapshot.children) {
                        child.getValue(Category::class.java)?.let { list.add(it) }
                    }
                    CoroutineScope(Dispatchers.IO).launch {
                        categoryDao.insertCategories(list)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Timber.e(error.toException(), "syncCategories failed")
                }
            })
    }

    private fun syncSongs(orderBy: String, limit: Int?) {
        val reference = database.getReference(DATA.SONGS)
        val query = if (limit != null) reference.orderByChild(orderBy).limitToLast(limit)
        else reference.orderByChild(orderBy)

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Song>()
                for (child in snapshot.children) {
                    child.getValue(Song::class.java)?.let {
                        it.key = child.key
                        list.add(it)
                    }
                }
                CoroutineScope(Dispatchers.IO).launch {
                    songDao.insertSongs(list)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.e(error.toException(), "syncSongs failed")
            }
        })
    }

    private fun syncAlbums(orderBy: String = DATA.TIMESTAMP) {
        val reference = database.getReference(DATA.ALBUMS)
        val query = if (orderBy.isNotEmpty()) reference.orderByChild(orderBy) else reference
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Album>()
                for (child in snapshot.children) {
                    child.getValue(Album::class.java)?.let { list.add(it) }
                }
                CoroutineScope(Dispatchers.IO).launch {
                    albumDao.insertAlbums(list)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.e(error.toException(), "syncAlbums failed")
            }
        })
    }

    private fun syncArtists(orderBy: String = DATA.TIMESTAMP) {
        val reference = database.getReference(DATA.ARTISTS)
        val query = if (orderBy.isNotEmpty()) reference.orderByChild(orderBy) else reference
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Artist>()
                for (child in snapshot.children) {
                    child.getValue(Artist::class.java)?.let { list.add(it) }
                }
                CoroutineScope(Dispatchers.IO).launch {
                    artistDao.insertArtists(list)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.e(error.toException(), "syncArtists failed")
            }
        })
    }

    private fun syncFavorites(userId: String) {
        if (userId.isEmpty()) return
        database.getReference(DATA.FAVORITES).child(userId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val favList = snapshot.children.mapNotNull { it.key }
                        .map { FavoriteEntity(userId, it) }
                    CoroutineScope(Dispatchers.IO).launch {
                        favoriteDao.deleteAllFavoritesForUser(userId)
                        favoriteDao.insertFavorites(favList)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Timber.e(error.toException(), "syncFavorites failed")
                }
            })
    }

    private fun syncInterested(userId: String, databaseName: String) {
        if (userId.isEmpty()) return
        database.getReference(DATA.INTERESTED).child(userId).child(databaseName)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = snapshot.children.mapNotNull { it.key }
                        .map { InterestedEntity(userId, databaseName, it) }
                    CoroutineScope(Dispatchers.IO).launch {
                        interestedDao.deleteAllInterestedForUser(userId, databaseName)
                        interestedDao.insertInterestedList(list)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Timber.e(error.toException(), "syncInterested failed")
                }
            })
    }
}
