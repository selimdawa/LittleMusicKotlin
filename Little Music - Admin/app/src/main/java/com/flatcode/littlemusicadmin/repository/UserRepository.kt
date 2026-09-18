package com.flatcode.littlemusicadmin.repository

import com.flatcode.littlemusicadmin.model.User
import com.flatcode.littlemusicadmin.utils.DATA
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
class UserRepository @Inject constructor(
    private val database: FirebaseDatabase
) {

    fun getUserInfo(): Flow<User> = callbackFlow {
        val ref = database.getReference(DATA.USERS).child(DATA.FirebaseUserUid)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                if (user != null) trySend(user)
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.e(error.toException(), "Error getting user info")
                close(error.toException())
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    fun getUsers(orderBy: String): Flow<List<User>> = callbackFlow {
        val ref = database.getReference(DATA.USERS).orderByChild(orderBy)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<User>()
                for (data in snapshot.children) {
                    val item = data.getValue(User::class.java)
                    if (item != null && item.id != DATA.FirebaseUserUid) list.add(item)
                }
                trySend(list.reversed())
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.e(error.toException(), "Error getting users")
                close(error.toException())
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    fun getUsersCount(): Flow<Int> = callbackFlow {
        val ref = database.getReference(DATA.USERS)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var count = 0
                for (data in snapshot.children) {
                    val item = data.getValue(User::class.java)
                    if (item?.id != null && item.id != DATA.FirebaseUserUid) count++
                }
                trySend(count)
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.e(error.toException(), "Error getting users count")
                close(error.toException())
            }
        }
        ref.addListenerForSingleValueEvent(listener)
        awaitClose { ref.removeEventListener(listener) }
    }
}
