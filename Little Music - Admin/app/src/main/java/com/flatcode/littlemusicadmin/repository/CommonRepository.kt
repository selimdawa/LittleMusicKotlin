package com.flatcode.littlemusicadmin.repository

import android.net.Uri
import com.flatcode.littlemusicadmin.utils.DATA
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class CommonRepository @Inject constructor(
    private val database: FirebaseDatabase
) {

    fun getNewKey(path: String): String? = database.getReference(path).push().key

    suspend fun uploadImage(imageUri: Uri, path: String): String? = suspendCancellableCoroutine { continuation ->
        try {
            MediaManager.get().upload(imageUri)
                .unsigned(DATA.CLOUDINARY_UPLOAD_PRESET)
                .option("public_id", path)
                .callback(object : UploadCallback {
                    override fun onStart(requestId: String) {}
                    override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {}
                    override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                        val secureUrl = resultData["secure_url"]?.toString()
                        if (continuation.isActive) continuation.resume(secureUrl)
                    }
                    override fun onError(requestId: String, error: ErrorInfo?) {
                        Timber.e("Cloudinary upload error: ${error?.description}")
                        if (continuation.isActive) continuation.resume(null)
                    }
                    override fun onReschedule(requestId: String, error: ErrorInfo?) {
                        if (continuation.isActive) continuation.resume(null)
                    }
                }).dispatch()
        } catch (e: Exception) {
            Timber.e(e, "Exception uploading image to Cloudinary")
            if (continuation.isActive) continuation.resume(null)
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
        val ref = database.getReference(DATA.TOOLS).child(DATA.PRIVACY_POLICY)
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
