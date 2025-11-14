package com.flatcode.littlemusicadmin.Activity

import android.app.Activity
import android.app.ProgressDialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.flatcode.littlemusicadmin.Model.Song
import com.flatcode.littlemusicadmin.R
import com.flatcode.littlemusicadmin.Unit.DATA
import com.flatcode.littlemusicadmin.Unit.THEME
import com.flatcode.littlemusicadmin.Unit.VOID
import com.flatcode.littlemusicadmin.databinding.ActivitySongEditBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SongEditActivity : AppCompatActivity() {

    private var binding: ActivitySongEditBinding? = null
    var activity: Activity = this@SongEditActivity
    private var songId: String? = null
    private var category: String? = null
    private var artist: String? = null
    private var album: String? = null
    private var categoryList: ArrayList<String>? = null
    private var categoryId: ArrayList<String>? = null
    private var artistList: ArrayList<String>? = null
    private var artistId: ArrayList<String>? = null
    private var albumList: ArrayList<String>? = null
    private var albumId: ArrayList<String>? = null
    private var dialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        THEME.setThemeOfApp(activity)
        super.onCreate(savedInstanceState)
        binding = ActivitySongEditBinding.inflate(layoutInflater)
        val view = binding!!.root
        setContentView(view)

        songId = intent.getStringExtra(DATA.SONG_ID)
        category = intent.getStringExtra(DATA.CATEGORY_ID)
        artist = intent.getStringExtra(DATA.ARTIST_ID)
        album = intent.getStringExtra(DATA.ALBUM_ID)

        dialog = ProgressDialog(activity)
        dialog!!.setTitle("Please wait...")
        dialog!!.setCanceledOnTouchOutside(false)

        loadCategories()
        loadArtists()
        loadAlbums()
        loadInfo()

        binding!!.toolbar.nameSpace.setText(R.string.edit_song)
        binding!!.toolbar.back.setOnClickListener { onBackPressed() }
        binding!!.category.setOnClickListener { categoryPickDialog() }
        binding!!.artist.setOnClickListener { artistPickDialog() }
        binding!!.album.setOnClickListener { albumPickDialog() }
        binding!!.toolbar.ok.setOnClickListener { validateData() }
    }

    private var name = DATA.EMPTY
    private var selectedCategoryId: String? = null
    private var selectedCategoryTitle: String? = null
    private var selectedArtistId: String? = null
    private var selectedArtistTitle: String? = null
    private var selectedAlbumId: String? = null
    private var selectedAlbumTitle: String? = null
    private fun validateData() {
        //get data
        name = binding!!.nameEt.text.toString().trim { it <= ' ' }

        //validate data
        if (TextUtils.isEmpty(name)) Toast.makeText(activity, "Enter Name...", Toast.LENGTH_SHORT)
            .show() else if (artist!!.isEmpty() && TextUtils.isEmpty(selectedArtistId)) Toast.makeText(
            activity, "Enter Artist...", Toast.LENGTH_SHORT
        )
            .show() else if (category!!.isEmpty() && TextUtils.isEmpty(selectedCategoryId)) Toast.makeText(
            activity, "Enter Category...", Toast.LENGTH_SHORT
        ).show() else if (album!!.isEmpty() && TextUtils.isEmpty(selectedAlbumId)) Toast.makeText(
            activity, "Enter Album...", Toast.LENGTH_SHORT
        ).show() else update()
    }

    private fun update() {
        dialog!!.setMessage("Updating Song...")
        dialog!!.show()
        val hashMap = HashMap<String?, Any>()
        hashMap[DATA.NAME] = DATA.EMPTY + name
        hashMap[DATA.CATEGORY_ID] = DATA.EMPTY + selectedCategoryId
        hashMap[DATA.ARTIST_ID] = DATA.EMPTY + selectedArtistId
        hashMap[DATA.ALBUM_ID] = DATA.EMPTY + selectedAlbumId
        val reference = FirebaseDatabase.getInstance().getReference(DATA.SONGS)
        reference.child(songId!!).updateChildren(hashMap).addOnSuccessListener {
            dialog!!.dismiss()
            Toast.makeText(activity, "Song updated...", Toast.LENGTH_SHORT).show()
        }.addOnCompleteListener {
            if (selectedCategoryId != category) {
                VOID.incrementItemCount(DATA.CATEGORIES, selectedCategoryId, DATA.SONGS_COUNT)
                if (category != null) VOID.incrementItemRemoveCount(
                    DATA.CATEGORIES, category, DATA.SONGS_COUNT
                )
            }
            if (selectedArtistId != artist) {
                VOID.incrementItemCount(DATA.ARTISTS, selectedArtistId, DATA.SONGS_COUNT)
                if (artist != null) VOID.incrementItemRemoveCount(
                    DATA.ARTISTS, artist, DATA.SONGS_COUNT
                )
            }
            if (selectedAlbumId != album) {
                VOID.incrementItemCount(DATA.ALBUMS, selectedAlbumId, DATA.SONGS_COUNT)
                if (album != null) VOID.incrementItemRemoveCount(
                    DATA.ALBUMS, album, DATA.SONGS_COUNT
                )
            }
            finish()
        }.addOnFailureListener { e: Exception ->
            dialog!!.dismiss()
            Toast.makeText(
                activity, "Failed to update db duo to : " + e.message, Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun loadInfo() {
        val reference = FirebaseDatabase.getInstance().getReference(DATA.SONGS)
        reference.child(songId!!).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val item = snapshot.getValue(Song::class.java)!!
                selectedCategoryId = DATA.EMPTY + snapshot.child(DATA.CATEGORY_ID).value
                selectedArtistId = DATA.EMPTY + snapshot.child(DATA.ARTIST_ID).value
                selectedAlbumId = DATA.EMPTY + snapshot.child(DATA.ALBUM_ID).value

                val name = item.name
                val duration = item.duration
                binding!!.nameEt.setText(name)
                binding!!.duration.text = VOID.convertDuration(duration!!.toLong())
                val refCategory = FirebaseDatabase.getInstance().getReference(DATA.CATEGORIES)
                refCategory.child(selectedCategoryId!!)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            //get category
                            val category = DATA.EMPTY + snapshot.child(DATA.NAME).value
                            binding!!.category.text = category
                        }

                        override fun onCancelled(error: DatabaseError) {}
                    })
                val refArtist = FirebaseDatabase.getInstance().getReference(DATA.ARTISTS)
                refArtist.child(selectedArtistId!!)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            //get artist
                            val artist = DATA.EMPTY + snapshot.child(DATA.NAME).value
                            binding!!.artist.text = artist
                        }

                        override fun onCancelled(error: DatabaseError) {}
                    })
                val refAlbum = FirebaseDatabase.getInstance().getReference(DATA.ALBUMS)
                refAlbum.child(selectedAlbumId!!)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            //get album
                            val album = DATA.EMPTY + snapshot.child(DATA.NAME).value
                            binding!!.album.text = album
                        }

                        override fun onCancelled(error: DatabaseError) {}
                    })
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun loadCategories() {
        categoryList = ArrayList()
        categoryId = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference(DATA.CATEGORIES)
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                categoryList!!.clear()
                categoryId!!.clear()
                for (data in snapshot.children) {
                    val id = DATA.EMPTY + data.child(DATA.ID).value
                    val name = DATA.EMPTY + data.child(DATA.NAME).value
                    categoryList!!.add(name)
                    categoryId!!.add(id)
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun loadArtists() {
        artistList = ArrayList()
        artistId = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference(DATA.ARTISTS)
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                artistList!!.clear()
                artistId!!.clear()
                for (data in snapshot.children) {
                    val id = DATA.EMPTY + data.child(DATA.ID).value
                    val name = DATA.EMPTY + data.child(DATA.NAME).value
                    artistList!!.add(name)
                    artistId!!.add(id)
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun loadAlbums() {
        albumList = ArrayList()
        albumId = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference(DATA.ALBUMS)
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                albumList!!.clear()
                albumId!!.clear()
                for (data in snapshot.children) {
                    val id = DATA.EMPTY + data.child(DATA.ID).value
                    val name = DATA.EMPTY + data.child(DATA.NAME).value
                    albumList!!.add(name)
                    albumId!!.add(id)
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun categoryPickDialog() {
        val categories = arrayOfNulls<String>(categoryList!!.size)
        for (i in categoryList!!.indices) {
            categories[i] = categoryList!![i]
        }
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Pick Category")
            .setItems(categories) { dialog: DialogInterface?, which: Int ->
                selectedCategoryTitle = categoryList!![which]
                selectedCategoryId = categoryId!![which]
                binding!!.category.text = selectedCategoryTitle
            }.show()
    }

    private fun artistPickDialog() {
        val artists = arrayOfNulls<String>(artistList!!.size)
        for (i in artistList!!.indices) {
            artists[i] = artistList!![i]
        }
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Pick Artist").setItems(artists) { dialog: DialogInterface?, which: Int ->
            selectedArtistTitle = artistList!![which]
            selectedArtistId = artistId!![which]
            binding!!.artist.text = selectedArtistTitle
        }.show()
    }

    private fun albumPickDialog() {
        val albums = arrayOfNulls<String>(albumList!!.size)
        for (i in albumList!!.indices) {
            albums[i] = albumList!![i]
        }
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Pick Album").setItems(albums) { dialog: DialogInterface?, which: Int ->
            selectedAlbumTitle = albumList!![which]
            selectedAlbumId = albumId!![which]
            binding!!.album.text = selectedAlbumTitle
        }.show()
    }
}