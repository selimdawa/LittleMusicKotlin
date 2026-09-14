package com.flatcode.littlemusic.repository

import com.flatcode.littlemusic.model.Album
import com.flatcode.littlemusic.model.Artist
import com.flatcode.littlemusic.model.Category
import com.flatcode.littlemusic.model.Song
import com.flatcode.littlemusic.utils.DATA
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicRepository @Inject constructor() {

    private val database = FirebaseDatabase.getInstance()

    fun getCategories(): Flow<List<Category>> = callbackFlow {
        val reference = database.getReference(DATA.CATEGORIES)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Category>()
                for (child in snapshot.children) {
                    child.getValue(Category::class.java)?.let { list.add(it) }
                }
                trySend(list)
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.e(error.toException(), "getCategories failed")
                close(error.toException())
            }
        }
        reference.addValueEventListener(listener)
        awaitClose { reference.removeEventListener(listener) }
    }

    fun getSongs(orderBy: String, limit: Int? = null): Flow<List<Song>> = callbackFlow {
        val reference = database.getReference(DATA.SONGS)
        val query = if (limit != null) reference.orderByChild(orderBy).limitToLast(limit)
        else reference.orderByChild(orderBy)

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Song>()
                for (child in snapshot.children) {
                    child.getValue(Song::class.java)?.let {
                        it.key = child.key
                        list.add(it)
                    }
                }
                if (orderBy != DATA.EDITORS_CHOICE) list.reverse()
                trySend(list)
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.e(error.toException(), "getSongs failed for $orderBy")
                close(error.toException())
            }
        }
        query.addValueEventListener(listener)
        awaitClose { query.removeEventListener(listener) }
    }

    fun getAlbums(orderBy: String): Flow<List<Album>> = callbackFlow {
        val reference = database.getReference(DATA.ALBUMS)
        val query = reference.orderByChild(orderBy)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Album>()
                for (child in snapshot.children) {
                    child.getValue(Album::class.java)?.let { list.add(it) }
                }
                list.reverse()
                trySend(list)
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.e(error.toException(), "getAlbums failed for $orderBy")
                close(error.toException())
            }
        }
        query.addValueEventListener(listener)
        awaitClose { query.removeEventListener(listener) }
    }

    fun getArtists(orderBy: String): Flow<List<Artist>> = callbackFlow {
        val reference = database.getReference(DATA.ARTISTS)
        val query = reference.orderByChild(orderBy)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Artist>()
                for (child in snapshot.children) {
                    child.getValue(Artist::class.java)?.let { list.add(it) }
                }
                list.reverse()
                trySend(list)
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.e(error.toException(), "getArtists failed for $orderBy")
                close(error.toException())
            }
        }
        query.addValueEventListener(listener)
        awaitClose { query.removeEventListener(listener) }
    }

    fun getInterestedIds(userId: String, databaseName: String): Flow<List<String>> = callbackFlow {
        val reference = database.getReference(DATA.INTERESTED).child(userId).child(databaseName)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<String>()
                for (child in snapshot.children) {
                    child.key?.let { list.add(it) }
                }
                trySend(list)
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.e(error.toException(), "getInterestedIds failed for $databaseName")
                close(error.toException())
            }
        }
        reference.addValueEventListener(listener)
        awaitClose { reference.removeEventListener(listener) }
    }

    fun getFavoriteIds(userId: String): Flow<List<String>> = callbackFlow {
        val reference = database.getReference(DATA.FAVORITES).child(userId)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<String>()
                for (child in snapshot.children) {
                    child.key?.let { list.add(it) }
                }
                trySend(list)
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.e(error.toException(), "getFavoriteIds failed")
                close(error.toException())
            }
        }
        reference.addValueEventListener(listener)
        awaitClose { reference.removeEventListener(listener) }
    }
}