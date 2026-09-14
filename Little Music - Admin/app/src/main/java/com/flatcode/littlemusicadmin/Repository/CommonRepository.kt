package com.flatcode.littlemusicadmin.Repository

import android.net.Uri
import com.flatcode.littlemusicadmin.Unit.DATA
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommonRepository @Inject constructor(
    private val database: FirebaseDatabase,
    private val storage: FirebaseStorage
) {

    fun getNewKey(path: String): String? = database.getReference(path).push().key

    suspend fun uploadImage(imageUri: Uri, path: String): String? {
        return try {
            val ref = storage.getReference(path)
            val uploadTask = ref.putFile(imageUri).await()
            uploadTask.storage.downloadUrl.await().toString()
        } catch (e: Exception) {
            Timber.e(e, "Error uploading image to $path")
            null
        }
    }

    fun getNameById(path: String, id: String): Flow<String?> = callbackFlow {
        val ref = database.getReference(path).child(id).child(DATA.NAME)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                trySend(snapshot.value?.toString())
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.e(error.toException(), "Error getting name by id $id in $path")
                close(error.toException())
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    fun getPrivacyPolicy(): Flow<String> = callbackFlow {
        val ref = database.getReference(DATA.PRIVACY_POLICY)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                trySend(snapshot.value?.toString() ?: "")
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.e(error.toException(), "Error getting privacy policy")
                close(error.toException())
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    fun getCount(path: String): Flow<Int> = callbackFlow {
        val ref = database.getReference(path)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                trySend(snapshot.childrenCount.toInt())
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.e(error.toException(), "Error getting count for $path")
                close(error.toException())
            }
        }
        ref.addListenerForSingleValueEvent(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    fun incrementItemCount(databaseName: String, id: String, childDB: String) {
        val ref = database.getReference(databaseName).child(id)
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val itemsCount = snapshot.child(childDB).value?.toString()?.toLongOrNull() ?: 0L
                val newItemsCount = itemsCount + 1
                ref.child(childDB).setValue(newItemsCount)
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.e(error.toException(), "Error incrementing item count for $databaseName/$id/$childDB")
            }
        })
    }

    fun incrementItemRemoveCount(databaseName: String, id: String, childDB: String) {
        val ref = database.getReference(databaseName).child(id)
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val itemsCount = snapshot.child(childDB).value?.toString()?.toLongOrNull() ?: 0L
                if (itemsCount > 0) {
                    val newItemsCount = itemsCount - 1
                    ref.child(childDB).setValue(newItemsCount)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.e(error.toException(), "Error decrementing item count for $databaseName/$id/$childDB")
            }
        })
    }
}
