package com.flatcode.littlemusic.repository

import com.flatcode.littlemusic.db.FavoriteDao
import com.flatcode.littlemusic.db.InterestedDao
import com.flatcode.littlemusic.db.UserDao
import com.flatcode.littlemusic.model.User
import com.flatcode.littlemusic.utils.DATA
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val userDao: UserDao,
    private val favoriteDao: FavoriteDao,
    private val interestedDao: InterestedDao
) {

    private val database = FirebaseDatabase.getInstance()

    fun getUserInfo(userId: String): Flow<User?> = callbackFlow {
        val repositoryScope = CoroutineScope(Dispatchers.IO)
        val localJob = repositoryScope.launch {
            userDao.getUserById(userId).collect { cachedUser ->
                if (cachedUser != null) {
                    trySend(cachedUser)
                }
            }
        }

        val reference = database.getReference(DATA.USERS).child(userId)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                if (user != null) {
                    trySend(user)
                    repositoryScope.launch {
                        userDao.insertUser(user)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.e(error.toException(), "getUserInfo failed for $userId")
            }
        }
        reference.addValueEventListener(listener)
        awaitClose {
            reference.removeEventListener(listener)
            localJob.cancel()
        }
    }

    fun getCount(userId: String): Flow<Long> {
        return favoriteDao.getFavoriteCount(userId).map { it.toLong() }
    }

    fun getInterestedCount(userId: String, databaseName: String): Flow<Long> {
        return interestedDao.getInterestedCount(userId, databaseName).map { it.toLong() }
    }
}
