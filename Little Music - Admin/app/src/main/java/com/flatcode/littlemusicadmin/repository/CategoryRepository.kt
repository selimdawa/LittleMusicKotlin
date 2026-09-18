package com.flatcode.littlemusicadmin.repository

import com.flatcode.littlemusicadmin.model.Category
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
class CategoryRepository @Inject constructor(
    private val database: FirebaseDatabase
) {

    fun getCategories(orderBy: String): Flow<List<Category>> = callbackFlow {
        val ref = database.getReference(DATA.CATEGORIES).orderByChild(orderBy)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Category>()
                for (data in snapshot.children) {
                    val item = data.getValue(Category::class.java)
                    if (item != null) list.add(item)
                }
                trySend(list.reversed())
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.e(error.toException(), "Error getting categories")
                close(error.toException())
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    fun getCategoriesCount(): Flow<Int> = callbackFlow {
        val ref = database.getReference(DATA.CATEGORIES)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var count = 0
                for (data in snapshot.children) {
                    val item = data.getValue(Category::class.java)
                    if (item?.id != null && item.publisher == DATA.FirebaseUserUid) count++
                }
                trySend(count)
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.e(error.toException(), "Error getting categories count")
                close(error.toException())
            }
        }
        ref.addListenerForSingleValueEvent(listener)
        awaitClose { ref.removeEventListener(listener) }
    }
}
