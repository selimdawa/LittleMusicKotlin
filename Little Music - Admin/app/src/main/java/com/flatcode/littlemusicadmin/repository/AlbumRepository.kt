package com.flatcode.littlemusicadmin.repository

import com.flatcode.littlemusicadmin.model.Album
import com.flatcode.littlemusicadmin.utils.DATA
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlbumRepository @Inject constructor(
    private val database: FirebaseDatabase,
    private val commonRepository: CommonRepository
) {

    fun getAlbums(orderBy: String): Flow<List<Album>> = callbackFlow {
        val ref = database.getReference(DATA.ALBUMS).orderByChild(orderBy)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Album>()
                for (data in snapshot.children) {
                    val item = data.getValue(Album::class.java)
                    if (item != null) list.add(item)
                }
                trySend(list.reversed())
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.e(error.toException(), "Error getting albums")
                close(error.toException())
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    fun getAlbumsByArtist(artistId: String, orderBy: String): Flow<List<Album>> = callbackFlow {
        val ref = database.getReference(DATA.ALBUMS).orderByChild(orderBy)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Album>()
                for (data in snapshot.children) {
                    val item = data.getValue(Album::class.java)
                    if (item?.artistId == artistId) {
                        list.add(item)
                    }
                }
                trySend(list.reversed())
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.e(error.toException(), "Error getting albums by artist")
                close(error.toException())
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    fun getAlbumsByCategory(categoryId: String, orderBy: String): Flow<List<Album>> = callbackFlow {
        val ref = database.getReference(DATA.ALBUMS).orderByChild(orderBy)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Album>()
                for (data in snapshot.children) {
                    val item = data.getValue(Album::class.java)
                    if (item?.categoryId == categoryId) {
                        list.add(item)
                    }
                }
                trySend(list.reversed())
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.e(error.toException(), "Error getting albums by category")
                close(error.toException())
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    fun getAlbumById(id: String): Flow<Album?> = callbackFlow {
        val ref = database.getReference(DATA.ALBUMS).child(id)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                trySend(snapshot.getValue(Album::class.java))
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.e(error.toException(), "Error getting album by id $id")
                close(error.toException())
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    suspend fun addAlbum(
        id: String,
        name: String,
        categoryId: String,
        artistId: String,
        imageUrl: String
    ): Boolean {
        return try {
            val ref = database.getReference(DATA.ALBUMS)
            val hashMap = HashMap<String, Any?>()
            hashMap[DATA.PUBLISHER] = DATA.FirebaseUserUid
            hashMap[DATA.TIMESTAMP] = System.currentTimeMillis()
            hashMap[DATA.ID] = id
            hashMap[DATA.NAME] = name
            hashMap[DATA.CATEGORY_ID] = categoryId
            hashMap[DATA.ARTIST_ID] = artistId
            hashMap[DATA.IMAGE] = imageUrl
            hashMap[DATA.INTERESTED_COUNT] = DATA.ZERO
            hashMap[DATA.SONGS_COUNT] = DATA.ZERO

            ref.child(id).setValue(hashMap).await()

            // Increment counts
            commonRepository.incrementItemCount(DATA.ARTISTS, artistId, DATA.ALBUMS_COUNT)
            commonRepository.incrementItemCount(DATA.CATEGORIES, categoryId, DATA.ALBUMS_COUNT)
            true
        } catch (e: Exception) {
            Timber.e(e, "Error adding album")
            false
        }
    }

    suspend fun updateAlbum(
        id: String,
        name: String,
        categoryId: String,
        artistId: String,
        imageUrl: String?,
        oldCategoryId: String?,
        oldArtistId: String?
    ): Boolean {
        return try {
            val ref = database.getReference(DATA.ALBUMS).child(id)
            val hashMap = HashMap<String, Any?>()
            hashMap[DATA.NAME] = name
            hashMap[DATA.CATEGORY_ID] = categoryId
            hashMap[DATA.ARTIST_ID] = artistId
            if (imageUrl != null) {
                hashMap[DATA.IMAGE] = imageUrl
            }

            ref.updateChildren(hashMap).await()

            // Update counts if category or artist changed
            if (oldCategoryId != null && oldCategoryId != categoryId) {
                commonRepository.incrementItemRemoveCount(DATA.CATEGORIES, oldCategoryId, DATA.ALBUMS_COUNT)
                commonRepository.incrementItemCount(DATA.CATEGORIES, categoryId, DATA.ALBUMS_COUNT)
            }
            if (oldArtistId != null && oldArtistId != artistId) {
                commonRepository.incrementItemRemoveCount(DATA.ARTISTS, oldArtistId, DATA.ALBUMS_COUNT)
                commonRepository.incrementItemCount(DATA.ARTISTS, artistId, DATA.ALBUMS_COUNT)
            }

            true
        } catch (e: Exception) {
            Timber.e(e, "Error updating album $id")
            false
        }
    }
}
