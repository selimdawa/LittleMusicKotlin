package com.flatcode.littlemusic.utils

import android.widget.ImageView
import android.widget.TextView
import com.flatcode.littlemusic.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


fun ImageView.isFavorite(id: String?, userId: String?) {
    val reference: DatabaseReference =
        FirebaseDatabase.getInstance().reference.child(DATA.FAVORITES).child(userId!!)
    reference.addValueEventListener(object : ValueEventListener {
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

fun String.incrementLovesCount() {
    val ref: DatabaseReference = FirebaseDatabase.getInstance().getReference(DATA.SONGS)
    ref.child(this).addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            var lovesCount = DATA.EMPTY + snapshot.child(DATA.LOVES_COUNT).value
            if (lovesCount == DATA.EMPTY || lovesCount == DATA.NULL) lovesCount = "0"
            val newLovesCount = lovesCount.toLong() + 1
            val hashMap = HashMap<String, Any>()
            hashMap[DATA.LOVES_COUNT] = newLovesCount
            val reference: DatabaseReference =
                FirebaseDatabase.getInstance().getReference(DATA.SONGS)
            reference.child(this@incrementLovesCount).updateChildren(hashMap)
        }

        override fun onCancelled(error: DatabaseError) {}
    })
}

fun String.incrementLovesRemoveCount() {
    val ref: DatabaseReference = FirebaseDatabase.getInstance().getReference(DATA.SONGS)
    ref.child(this).addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            var lovesCount = DATA.EMPTY + snapshot.child("lovesCount").value
            if (lovesCount == DATA.EMPTY || lovesCount == DATA.NULL) {
                lovesCount = "0"
            }
            val removeLovesCount = lovesCount.toLong() - 1
            val hashMap = HashMap<String, Any>()
            hashMap["lovesCount"] = removeLovesCount
            val reference: DatabaseReference =
                FirebaseDatabase.getInstance().getReference(DATA.SONGS)
            reference.child(this@incrementLovesRemoveCount).updateChildren(hashMap)
        }

        override fun onCancelled(error: DatabaseError) {}
    })
}

fun ImageView.checkFavorite(id: String?) {
    if (this.tag == "add") {
        FirebaseDatabase.getInstance().reference.child(DATA.FAVORITES).child(DATA.FirebaseUserUid)
            .child(id!!).setValue(true)
    } else {
        FirebaseDatabase.getInstance().reference.child(DATA.FAVORITES).child(DATA.FirebaseUserUid)
            .child(id!!).removeValue()
    }
}

fun ImageView.checkInterested(type: String?, id: String?) {
    if (this.tag == "add") {
        id?.incrementInterestedCount(type)
        FirebaseDatabase.getInstance().reference.child(DATA.INTERESTED).child(DATA.FirebaseUserUid)
            .child(type!!).child(id!!).setValue(true)
    } else {
        id?.incrementInterestedRemoveCount(type)
        FirebaseDatabase.getInstance().reference.child(DATA.INTERESTED).child(DATA.FirebaseUserUid)
            .child(type!!).child(id!!).removeValue()
    }
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
    val reference: DatabaseReference =
        FirebaseDatabase.getInstance().reference.child(DATA.LOVES).child(id!!)
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

fun ImageView.isInterested(id: String?, type: String?) {
    val reference: DatabaseReference =
        FirebaseDatabase.getInstance().getReference(DATA.INTERESTED).child(DATA.FirebaseUserUid)
            .child(type!!)
    reference.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            if (dataSnapshot.child(id!!).exists()) {
                this@isInterested.setImageResource(R.drawable.ic_star_selected)
                this@isInterested.tag = "added"
            } else {
                this@isInterested.setImageResource(R.drawable.ic_star_unselected)
                this@isInterested.tag = "add"
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {}
    })
}

fun TextView.nrLoves(id: String?) {
    val reference: DatabaseReference =
        FirebaseDatabase.getInstance().reference.child(DATA.LOVES).child(id!!)
    reference.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            this@nrLoves.text = dataSnapshot.childrenCount.toString()
        }

        override fun onCancelled(databaseError: DatabaseError) {}
    })
}

fun String.incrementViewCount() {
    val ref: DatabaseReference = FirebaseDatabase.getInstance().getReference(DATA.SONGS)
    ref.child(this).addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            var viewsCount = DATA.EMPTY + snapshot.child(DATA.VIEWS_COUNT).value
            if (viewsCount == DATA.EMPTY || viewsCount == DATA.NULL) {
                viewsCount = "0"
            }
            val newViewsCount = viewsCount.toLong() + 1
            val hashMap = HashMap<String, Any>()
            hashMap[DATA.VIEWS_COUNT] = newViewsCount
            val ref2: DatabaseReference = FirebaseDatabase.getInstance().getReference(DATA.SONGS)
            ref2.child(this@incrementViewCount).updateChildren(hashMap)
        }

        override fun onCancelled(error: DatabaseError) {}
    })
}

fun String.incrementInterestedCount(type: String?) {
    val ref: DatabaseReference = FirebaseDatabase.getInstance().getReference(type!!)
    ref.child(this).addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            var interestedCount = DATA.EMPTY + snapshot.child(DATA.INTERESTED_COUNT).value
            if (interestedCount == DATA.EMPTY || interestedCount == DATA.NULL) {
                interestedCount = "0"
            }
            val newInterestedCount = interestedCount.toLong() + 1
            val hashMap = HashMap<String, Any>()
            hashMap[DATA.INTERESTED_COUNT] = newInterestedCount
            val reference: DatabaseReference = FirebaseDatabase.getInstance().getReference(type)
            reference.child(this@incrementInterestedCount).updateChildren(hashMap)
        }

        override fun onCancelled(error: DatabaseError) {}
    })
}

fun String.incrementInterestedRemoveCount(type: String?) {
    val ref: DatabaseReference = FirebaseDatabase.getInstance().getReference(type!!)
    ref.child(this).addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            var interestedCount = DATA.EMPTY + snapshot.child(DATA.INTERESTED_COUNT).value
            if (interestedCount == DATA.EMPTY || interestedCount == DATA.NULL) {
                interestedCount = "0"
            }
            val removeInterestedCount = interestedCount.toLong() - 1
            val hashMap = HashMap<String, Any>()
            hashMap[DATA.INTERESTED_COUNT] = removeInterestedCount
            val reference: DatabaseReference = FirebaseDatabase.getInstance().getReference(type)
            reference.child(this@incrementInterestedRemoveCount).updateChildren(hashMap)
        }

        override fun onCancelled(error: DatabaseError) {}
    })
}

fun TextView.dataName(database: String?, dataId: String?) {
    val reference: DatabaseReference = FirebaseDatabase.getInstance().getReference(database!!)
    reference.child(dataId!!).addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val name = snapshot.child(DATA.NAME).value?.toString() ?: ""
            this@dataName.text = name
        }

        override fun onCancelled(error: DatabaseError) {}
    })
}