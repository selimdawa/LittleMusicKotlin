package com.flatcode.littlemusicadmin.ui.profile

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.flatcode.littlemusicadmin.model.Category
import com.flatcode.littlemusicadmin.R
import com.flatcode.littlemusicadmin.utils.*
import com.flatcode.littlemusicadmin.databinding.ActivityProfileBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.MessageFormat

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    var context: Context = this@ProfileActivity
    var profileId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = intent
        profileId = intent.getStringExtra(DATA.PROFILE_ID)

        loadUserInfo()
        nrFavorites
        if (profileId == DATA.FirebaseUserUid) {
            binding.edit.visibility = View.VISIBLE
            binding.edit.setImageResource(R.drawable.ic_edit_white)
            binding.edit.setOnClickListener { context.intent1(ProfileEditActivity::class.java) }
            getNrItems(DATA.ALBUMS, binding.numberAlbums)
            getNrItems(DATA.ARTISTS, binding.numberArtists)
            getNrItems(DATA.CATEGORIES, binding.numberCategories)
        } else {
            nrInterested(DATA.ALBUMS, binding.numberAlbums)
            nrInterested(DATA.ARTISTS, binding.numberArtists)
            nrInterested(DATA.CATEGORIES, binding.numberCategories)
        }
        binding.back.setOnClickListener { onBackPressed() }
    }

    private fun loadUserInfo() {
        val reference = FirebaseDatabase.getInstance().getReference(DATA.USERS)
        reference.child(profileId!!).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val username = DATA.EMPTY + snapshot.child(DATA.USER_NAME).value
                val profileImage = DATA.EMPTY + snapshot.child(DATA.PROFILE_IMAGE).value
                binding.username.text = username
                binding.profile.glide(true, profileImage)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun nrInterested(database: String?, text: TextView) {
        val reference = FirebaseDatabase.getInstance().getReference(DATA.INTERESTED)
            .child(profileId!!).child(database!!)
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
                    val item = data.getValue(Category::class.java)!!
                    if (item.publisher == profileId) i++
                }
                text.text = MessageFormat.format("{0}{1}", DATA.EMPTY, i)
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private val nrFavorites: Unit
        get() {
            val ref = FirebaseDatabase.getInstance().getReference(DATA.FAVORITES).child(profileId!!)
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    binding.numberFavorites.text =
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
