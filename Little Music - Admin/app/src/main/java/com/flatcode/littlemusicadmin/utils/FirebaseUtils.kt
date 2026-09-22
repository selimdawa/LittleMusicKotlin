package com.flatcode.littlemusicadmin.utils

import android.widget.ImageView
import android.widget.TextView
import com.flatcode.littlemusicadmin.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.MessageFormat

fun String?.incrementViewCount() {
    if (this == null) return
    val ref = FirebaseDatabase.getInstance().getReference(DATA.SONGS)
    ref.child(this).addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            var viewsCount = DATA.EMPTY + snapshot.child(DATA.VIEWS_COUNT).value
            if (viewsCount == DATA.EMPTY || viewsCount == DATA.NULL) {
                viewsCount = "0"
            }
            val newViewsCount = viewsCount.toLong() + 1
            val hashMap = HashMap<String?, Any>()
            hashMap[DATA.VIEWS_COUNT] = newViewsCount
            val reference = FirebaseDatabase.getInstance().getReference(DATA.SONGS)
            reference.child(this@incrementViewCount).updateChildren(hashMap)
        }

        override fun onCancelled(error: DatabaseError) {}
    })
}

fun String?.incrementLovesCount() {
    if (this == null) return
    val ref = FirebaseDatabase.getInstance().getReference(DATA.SONGS)
    ref.child(this).addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            var lovesCount = DATA.EMPTY + snapshot.child(DATA.LOVES_COUNT).value
            if (lovesCount == DATA.EMPTY || lovesCount == DATA.NULL) {
                lovesCount = "0"
            }
            val newLovesCount = lovesCount.toLong() + 1
            val hashMap = HashMap<String?, Any>()
            hashMap[DATA.LOVES_COUNT] = newLovesCount
            val reference = FirebaseDatabase.getInstance().getReference(DATA.SONGS)
            reference.child(this@incrementLovesCount).updateChildren(hashMap)
        }

        override fun onCancelled(error: DatabaseError) {}
    })
}

fun String?.incrementLovesRemoveCount() {
    if (this == null) return
    val ref = FirebaseDatabase.getInstance().getReference(DATA.SONGS)
    ref.child(this).addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            var lovesCount = DATA.EMPTY + snapshot.child(DATA.LOVES_COUNT).value
            if (lovesCount == DATA.EMPTY || lovesCount == DATA.NULL) {
                lovesCount = "0"
            }
            val removeLovesCount = lovesCount.toLong() - 1
            val hashMap = HashMap<String, Any>()
            hashMap[DATA.LOVES_COUNT] = removeLovesCount
            val reference = FirebaseDatabase.getInstance().getReference(DATA.SONGS)
            reference.child(this@incrementLovesRemoveCount).updateChildren(hashMap)
        }

        override fun onCancelled(error: DatabaseError) {}
    })
}

fun String?.incrementItemCount(database: String, childDb: String) {
    if (this == null) return
    val ref = FirebaseDatabase.getInstance().getReference(database)
    ref.child(this).addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            var itemsCount = DATA.EMPTY + snapshot.child(childDb).value
            if (itemsCount == DATA.EMPTY || itemsCount == DATA.NULL) {
                itemsCount = DATA.EMPTY + DATA.ZERO
            }
            val newItemsCount = itemsCount.toLong() + 1
            val hashMap = HashMap<String?, Any>()
            hashMap[childDb] = newItemsCount
            val reference = FirebaseDatabase.getInstance().getReference(database)
            reference.child(this@incrementItemCount).updateChildren(hashMap)
        }

        override fun onCancelled(error: DatabaseError) {}
    })
}

fun String?.incrementItemRemoveCount(database: String, childDb: String) {
    if (this == null) return
    val ref = FirebaseDatabase.getInstance().getReference(database)
    ref.child(this).addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            var lovesCount = DATA.EMPTY + snapshot.child(childDb).value
            if (lovesCount == DATA.EMPTY || lovesCount == DATA.NULL) lovesCount =
                DATA.EMPTY + DATA.ZERO

            val i = lovesCount.toInt()
            if (i > 0) {
                val removeLovesCount = lovesCount.toInt() - 1
                val hashMap = HashMap<String?, Any>()
                hashMap[childDb] = removeLovesCount

                val reference = FirebaseDatabase.getInstance().getReference(database)
                reference.child(this@incrementItemRemoveCount).updateChildren(hashMap)
            }
        }

        override fun onCancelled(error: DatabaseError) {}
    })
}

fun ImageView.isFavorite(id: String?, userId: String?) {
    val ref = FirebaseDatabase.getInstance().reference.child(DATA.FAVORITES).child(userId!!)
    ref.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            if (dataSnapshot.child(id!!).exists()) {
                this@isFavorite.setImageResource(R.drawable.ic_star_selected)
                this@isFavorite.tag = "added"
            } else {
                this@isFavorite.setImageResource(R.drawable.ic_star_unselected)
                this@isFavorite.tag = "add"
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {}
    })
}

fun ImageView.checkFavorite(id: String?) {
    if (this.tag == "add") FirebaseDatabase.getInstance().getReference(DATA.FAVORITES)
        .child(DATA.FirebaseUserUid).child(id!!).setValue(true) else FirebaseDatabase.getInstance()
        .getReference(DATA.FAVORITES).child(DATA.FirebaseUserUid).child(id!!).removeValue()
}

fun ImageView.checkLove(id: String?) {
    if (this.tag == "love") {
        FirebaseDatabase.getInstance().getReference(DATA.LOVES).child(id!!)
            .child(DATA.FirebaseUserUid).setValue(true)
        id.incrementLovesCount()
    } else {
        FirebaseDatabase.getInstance().getReference(DATA.LOVES).child(id!!)
            .child(DATA.FirebaseUserUid).removeValue()
        id.incrementLovesRemoveCount()
    }
}

fun ImageView.isLoves(id: String?) {
    val reference = FirebaseDatabase.getInstance().reference.child(DATA.LOVES).child(id!!)
    reference.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            if (dataSnapshot.child(DATA.FirebaseUserUid).exists()) {
                this@isLoves.setImageResource(R.drawable.ic_heart_selected)
                this@isLoves.tag = "loved"
            } else {
                this@isLoves.setImageResource(R.drawable.ic_heart_unselected)
                this@isLoves.tag = "love"
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {}
    })
}

fun TextView.nrLoves(id: String?) {
    val reference = FirebaseDatabase.getInstance().reference.child(DATA.LOVES).child(id!!)
    reference.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            this@nrLoves.text = MessageFormat.format(" {0} ", dataSnapshot.childrenCount)
        }

        override fun onCancelled(databaseError: DatabaseError) {}
    })
}

fun TextView.dataName(database: String?, dataId: String?) {
    val reference = FirebaseDatabase.getInstance().getReference(database!!)
    reference.child(dataId!!).addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val nameValue = DATA.EMPTY + snapshot.child(DATA.NAME).value
            this@dataName.text = MessageFormat.format("{0}{1}", DATA.EMPTY, nameValue)
        }

        override fun onCancelled(error: DatabaseError) {}
    })
}
