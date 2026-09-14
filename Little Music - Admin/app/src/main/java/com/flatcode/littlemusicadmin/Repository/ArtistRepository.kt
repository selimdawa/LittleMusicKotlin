package com.flatcode.littlemusicadmin.Repository

import com.flatcode.littlemusicadmin.Model.Artist
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
class ArtistRepository @Inject constructor(
    private val database: FirebaseDatabase
) {

    fun getArtists(orderBy: String): Flow<List<Artist>> = callbackFlow {
        val ref = database.getReference(DATA.ARTISTS).orderByChild(orderBy)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Artist>()
                for (data in snapshot.children) {
                    val item = data.getValue(Artist::class.java)
                    if (item != null) list.add(item)
                }
                trySend(list.reversed())
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.e(error.toException(), "Error getting artists")
                close(error.toException())
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }
}
