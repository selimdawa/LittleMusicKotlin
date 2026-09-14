package com.flatcode.littlemusicadmin.Repository

import com.flatcode.littlemusicadmin.Model.Song
import com.flatcode.littlemusicadmin.Unit.DATA
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
class SongRepository @Inject constructor(
    private val database: FirebaseDatabase
) {

    fun getSongs(orderBy: String): Flow<List<Song>> = callbackFlow {
        val ref = database.getReference(DATA.SONGS).orderByChild(orderBy)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Song>()
                for (data in snapshot.children) {
                    val item = data.getValue(Song::class.java)
                    if (item?.id != null) {
                        item.key = data.key
                        list.add(item)
                    }
                }
                trySend(list)
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.e(error.toException(), "Error getting songs")
                close(error.toException())
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    fun getSongsByAlbum(albumId: String, orderBy: String): Flow<List<Song>> = callbackFlow {
        val ref = database.getReference(DATA.SONGS).orderByChild(orderBy)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Song>()
                for (data in snapshot.children) {
                    val item = data.getValue(Song::class.java)
                    if (item?.id != null && item.albumId == albumId) {
                        item.key = data.key
                        list.add(item)
                    }
                }
                trySend(list)
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.e(error.toException(), "Error getting songs by album")
                close(error.toException())
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    fun getSongsByArtist(artistId: String, orderBy: String): Flow<List<Song>> = callbackFlow {
        val ref = database.getReference(DATA.SONGS).orderByChild(orderBy)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Song>()
                for (data in snapshot.children) {
                    val item = data.getValue(Song::class.java)
                    if (item?.id != null && item.artistId == artistId) {
                        item.key = data.key
                        list.add(item)
                    }
                }
                trySend(list)
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.e(error.toException(), "Error getting songs by artist")
                close(error.toException())
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    fun getSongsByCategory(categoryId: String, orderBy: String): Flow<List<Song>> = callbackFlow {
        val ref = database.getReference(DATA.SONGS).orderByChild(orderBy)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Song>()
                for (data in snapshot.children) {
                    val item = data.getValue(Song::class.java)
                    if (item?.id != null && item.categoryId == categoryId) {
                        item.key = data.key
                        list.add(item)
                    }
                }
                trySend(list)
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.e(error.toException(), "Error getting songs by category")
                close(error.toException())
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    fun getSongsAndEditorsChoiceCount(): Flow<Pair<Int, Int>> = callbackFlow {
        val ref = database.getReference(DATA.SONGS)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var total = 0
                var editorsChoice = 0
                for (data in snapshot.children) {
                    val item = data.getValue(Song::class.java)
                    if (item?.id != null) {
                        total++
                        if (item.editorsChoice != 0 && item.publisher == DATA.FirebaseUserUid) {
                            editorsChoice++
                        }
                    }
                }
                trySend(Pair(total, editorsChoice))
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.e(error.toException(), "Error getting songs count")
                close(error.toException())
            }
        }
        ref.addListenerForSingleValueEvent(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    fun getFavorites(orderBy: String): Flow<List<Song>> = callbackFlow {
        val favoritesRef = database.getReference(DATA.FAVORITES).child(DATA.FirebaseUserUid)
        val songsRef = database.getReference(DATA.SONGS).orderByChild(orderBy)

        val listener = object : ValueEventListener {
            override fun onDataChange(favoritesSnapshot: DataSnapshot) {
                val favoriteIds = favoritesSnapshot.children.mapNotNull { it.key }
                songsRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(songsSnapshot: DataSnapshot) {
                        val list = mutableListOf<Song>()
                        for (data in songsSnapshot.children) {
                            val item = data.getValue(Song::class.java)
                            if (item?.id != null && favoriteIds.contains(item.id)) {
                                item.key = data.key
                                list.add(item)
                            }
                        }
                        trySend(list)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Timber.e(error.toException(), "Error getting favorites songs")
                        close(error.toException())
                    }
                })
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.e(error.toException(), "Error getting favorites list")
                close(error.toException())
            }
        }
        favoritesRef.addValueEventListener(listener)
        awaitClose { favoritesRef.removeEventListener(listener) }
    }

    fun getFavoritesCount(): Flow<Int> = callbackFlow {
        val ref = database.getReference(DATA.FAVORITES).child(DATA.FirebaseUserUid)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                trySend(snapshot.childrenCount.toInt())
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.e(error.toException(), "Error getting favorites count")
                close(error.toException())
            }
        }
        ref.addListenerForSingleValueEvent(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    fun getSliderShow(): Flow<Map<String, String>> = callbackFlow {
        val ref = database.getReference(DATA.SLIDER_SHOW)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val map = mutableMapOf<String, String>()
                for (data in snapshot.children) {
                    val key = data.key
                    val value = data.value?.toString()
                    if (key != null && value != null) {
                        map[key] = value
                    }
                }
                trySend(map)
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.e(error.toException(), "Error getting slider show")
                close(error.toException())
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }
}
