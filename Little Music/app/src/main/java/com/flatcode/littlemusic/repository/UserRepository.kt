package com.flatcode.littlemusic.repository

import com.flatcode.littlemusic.model.User
import com.flatcode.littlemusic.utils.DATA
import com.google.firebase.auth.FirebaseAuth
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
class UserRepository @Inject constructor() {

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()

    fun getUserInfo(userId: String): Flow<User?> = callbackFlow {
        val reference = database.getReference(DATA.USERS).child(userId)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                trySend(snapshot.getValue(User::class.java))
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.e(error.toException(), "getUserInfo failed for $userId")
                close(error.toException())
            }
        }
        reference.addValueEventListener(listener)
        awaitClose { reference.removeEventListener(listener) }
    }

    fun getCount(userId: String, path: String): Flow<Long> = callbackFlow {
        val reference = database.getReference(path).child(userId)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                trySend(snapshot.childrenCount)
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.e(error.toException(), "getCount failed for $path")
                close(error.toException())
            }
        }
        reference.addValueEventListener(listener)
        awaitClose { reference.removeEventListener(listener) }
    }

    fun getInterestedCount(userId: String, databaseName: String): Flow<Long> = callbackFlow {
        val reference = database.getReference(DATA.INTERESTED).child(userId).child(databaseName)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                trySend(snapshot.childrenCount)
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.e(error.toException(), "getInterestedCount failed for $databaseName")
                close(error.toException())
            }
        }
        reference.addValueEventListener(listener)
        awaitClose { reference.removeEventListener(listener) }
    }
}