package com.flatcode.littlemusicadmin.Activity

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.flatcode.littlemusicadmin.Model.Category
import com.flatcode.littlemusicadmin.R
import com.flatcode.littlemusicadmin.Unit.CLASSv
import com.flatcode.littlemusicadmin.Unit.DATAv
import com.flatcode.littlemusicadmin.Unit.THEME
import com.flatcode.littlemusicadmin.Unit.VOID
import com.flatcode.littlemusicadmin.databinding.ActivityProfileBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.MessageFormat

class ProfileActivity : AppCompatActivity() {

    private var binding: ActivityProfileBinding? = null
    var context: Context = this@ProfileActivity
    var profileId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        THEME.setThemeOfApp(context)
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(
            layoutInflater
        )
        val view = binding!!.root
        setContentView(view)
        val intent = intent
        profileId = intent.getStringExtra(DATAv.PROFILE_ID)
        loadUserInfo()
        nrFavorites
        if (profileId == DATAv.FirebaseUserUid) {
            binding!!.edit.visibility = View.VISIBLE
            binding!!.edit.setImageResource(R.drawable.ic_edit_white)
            binding!!.edit.setOnClickListener { v: View? ->
                VOID.Intent1(
                    context,
                    CLASSv.PROFILE_EDIT
                )
            }
            getNrItems(DATAv.ALBUMS, binding!!.numberAlbums)
            getNrItems(DATAv.ARTISTS, binding!!.numberArtists)
            getNrItems(DATAv.CATEGORIES, binding!!.numberCategories)
        } else {
            nrInterested(DATAv.ALBUMS, binding!!.numberAlbums)
            nrInterested(DATAv.ARTISTS, binding!!.numberArtists)
            nrInterested(DATAv.CATEGORIES, binding!!.numberCategories)
        }
        binding!!.back.setOnClickListener { v: View? -> onBackPressed() }
    }

    private fun loadUserInfo() {
        val reference = FirebaseDatabase.getInstance().getReference(DATAv.USERS)
        reference.child(profileId!!).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                //String email = DATA.EMPTY + snapshot.child(DATA.EMAIL).getValue();
                val username = DATAv.EMPTY + snapshot.child(DATAv.USER_NAME).value
                val profileImage = DATAv.EMPTY + snapshot.child(DATAv.PROFILE_IMAGE).value
                //String timestamp = DATA.EMPTY + snapshot.child(DATA.TIMESTAMP).getValue();
                //String id = DATA.EMPTY + snapshot.child(DATA.ID).getValue();
                //int version = DATA.ZERO + snapshot.child(DATA.VERSION).getValue();
                binding!!.username.text = username
                VOID.Glide(true, context, profileImage, binding!!.profile)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun nrInterested(database: String?, text: TextView) {
        val reference = FirebaseDatabase.getInstance().getReference(DATAv.INTERESTED).child(
            profileId!!
        ).child(database!!)
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                text.text = MessageFormat.format("{0}", dataSnapshot.childrenCount)
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun getNrItems(database: String?, text: TextView) {
        val reference = FirebaseDatabase.getInstance().getReference(database!!)
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var i = 0
                for (data in dataSnapshot.children) {
                    val item = data.getValue(
                        Category::class.java
                    )!!
                    if (item.publisher == profileId) i++
                }
                text.text = MessageFormat.format("{0}{1}", DATAv.EMPTY, i)
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private val nrFavorites: Unit
        private get() {
            val reference = FirebaseDatabase.getInstance().getReference(DATAv.FAVORITES).child(
                profileId!!
            )
            reference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    binding!!.numberFavorites.text =
                        MessageFormat.format("{0}", dataSnapshot.childrenCount)
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
        }

    override fun onRestart() {
        loadUserInfo()
        super.onRestart()
    }

    override fun onResume() {
        loadUserInfo()
        super.onResume()
    }
}