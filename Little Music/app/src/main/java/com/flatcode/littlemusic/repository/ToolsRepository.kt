package com.flatcode.littlemusic.repository

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
class ToolsRepository @Inject constructor() {

    private val database = FirebaseDatabase.getInstance()

    fun getPrivacyPolicy(): Flow<String> = callbackFlow {
        val reference = database.getReference(DATA.TOOLS).child(DATA.PRIVACY_POLICY)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                trySend(snapshot.value?.toString() ?: "")
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.e(error.toException(), "getPrivacyPolicy failed")
                close(error.toException())
            }
        }
        reference.addValueEventListener(listener)
        awaitClose { reference.removeEventListener(listener) }
    }

    fun getSliderImages(): Flow<List<String>> = callbackFlow {
        val reference = database.getReference(DATA.SLIDER_SHOW)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val images = snapshot.children.mapNotNull { it.child(DATA.IMAGE).value?.toString() }
                trySend(images)
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.e(error.toException(), "getSliderImages failed")
                close(error.toException())
            }
        }
        reference.addValueEventListener(listener)
        awaitClose { reference.removeEventListener(listener) }
    }
}