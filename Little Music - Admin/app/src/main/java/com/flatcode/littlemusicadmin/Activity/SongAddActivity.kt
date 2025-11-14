package com.flatcode.littlemusicadmin.Activity

import android.app.Activity
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.flatcode.littlemusicadmin.R
import com.flatcode.littlemusicadmin.Unit.DATA
import com.flatcode.littlemusicadmin.Unit.THEME
import com.flatcode.littlemusicadmin.Unit.VOID
import com.flatcode.littlemusicadmin.databinding.ActivitySongAddBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask

class SongAddActivity : AppCompatActivity() {
    private var binding: ActivitySongAddBinding? = null
    var activity: Activity = this@SongAddActivity
    var audioUri: Uri? = null
    private var uploadsTask: StorageTask<*>? = null
    var metadataRetriever: MediaMetadataRetriever? = null

    //byte[] art;
    var nameSong: String? = null
    var durations //album_art = "",;
            : String? = null
    private var dialog: ProgressDialog? = null
    private var categoryId: ArrayList<String>? = null
    private var categoryList: ArrayList<String>? = null
    private var albumId: ArrayList<String>? = null
    private var albumList: ArrayList<String>? = null
    private var artistId: ArrayList<String>? = null
    private var artistList: ArrayList<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        THEME.setThemeOfApp(activity)
        super.onCreate(savedInstanceState)
        binding = ActivitySongAddBinding.inflate(layoutInflater)
        val view = binding!!.root
        setContentView(view)

        dialog = ProgressDialog(this)
        dialog!!.setTitle("Please wait...")
        dialog!!.setCanceledOnTouchOutside(false)

        loadCategories()
        loadAlbums()
        loadArtists()

        binding!!.toolbar.nameSpace.setText(R.string.add_new_song)
        binding!!.toolbar.back.setOnClickListener { onBackPressed() }
        binding!!.category.setOnClickListener { categoryPickDialog() }
        binding!!.album.setOnClickListener { albumPickDialog() }
        binding!!.artist.setOnClickListener { artistPickDialog() }
        binding!!.chooseSong.setOnClickListener { openAudioFiles() }
        binding!!.toolbar.ok.setOnClickListener { validateData() }
        metadataRetriever = MediaMetadataRetriever()
    }

    private var name = DATA.EMPTY
    private fun validateData() {
        //get data
        name = binding!!.nameEt.text.toString().trim { it <= ' ' }

        //validate data
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(activity, "Enter Name...", Toast.LENGTH_SHORT).show()
        } else if (TextUtils.isEmpty(selectedArtistTitle)) {
            Toast.makeText(activity, "Pick Artist...", Toast.LENGTH_SHORT).show()
        } else if (TextUtils.isEmpty(selectedAlbumTitle)) {
            Toast.makeText(activity, "Pick Album...", Toast.LENGTH_SHORT).show()
        } else if (TextUtils.isEmpty(selectedCategoryTitle)) {
            Toast.makeText(activity, "Pick Category...", Toast.LENGTH_SHORT).show()
        } else if (audioUri == null) {
            Toast.makeText(activity, "Pick Song...", Toast.LENGTH_SHORT).show()
        } else {
            uploadFileToDB()
        }
    }

    fun openAudioFiles() {
        val intent_upload = Intent()
        intent_upload.type = "audio/*"
        intent_upload.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent_upload, 101)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (data!!.data != null) {
                if (requestCode == 101) {
                    try {
                        audioUri = data.data
                        metadataRetriever!!.setDataSource(this, audioUri)
                        assert(metadataRetriever != null)
                        nameSong =
                            metadataRetriever!!.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
                        durations =
                            metadataRetriever!!.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                        binding!!.choose.setText(R.string.ok)
                        name = binding!!.nameEt.text.toString().trim { it <= ' ' }
                        if (TextUtils.isEmpty(name)) binding!!.nameEt.setText(nameSong)
                        binding!!.duration.text = VOID.convertDuration(durations!!.toLong())
                        name = nameSong!!
                    } catch (e: Exception) {
                        Toast.makeText(activity, "Error!", Toast.LENGTH_SHORT).show()
                        audioUri = null
                    }
                }
            } else {
                Toast.makeText(this, "Error ! ", Toast.LENGTH_SHORT).show()
                audioUri = null
            }
        }
    }

    fun uploadFileToDB() {
        val message = binding!!.choose.text.toString()
        if (message == "No file Selected") {
            Toast.makeText(this, "Please selected an image!", Toast.LENGTH_SHORT).show()
        } else {
            if (uploadsTask != null && uploadsTask!!.isInProgress) {
                Toast.makeText(this, "Songs uploads in already progress!", Toast.LENGTH_SHORT)
                    .show()
            } else {
                uploadFile()
            }
        }
    }

    private fun uploadFile() {
        Toast.makeText(this, "Uploads please wait!", Toast.LENGTH_SHORT).show()
        dialog!!.setMessage("Uploads Song...")
        dialog!!.show()
        val ref = FirebaseDatabase.getInstance().getReference(DATA.SONGS)
        val id = ref.push().key
        val filePathAndName = "Songs/$selectedArtistTitle/$id"
        val reference = FirebaseStorage.getInstance().getReference(filePathAndName)
        reference.putFile(audioUri!!)
            .addOnSuccessListener { taskSnapshot: UploadTask.TaskSnapshot ->
                val uriTask = taskSnapshot.storage.downloadUrl
                while (!uriTask.isSuccessful);
                val uploadedImageUrl = "" + uriTask.result
                dialog!!.dismiss()
                Toast.makeText(this, "Ok", Toast.LENGTH_SHORT).show()
                uploadInfoToDB(uploadedImageUrl, id, ref)
            }.addOnFailureListener { e: Exception ->
                dialog!!.dismiss()
                Toast.makeText(this, "Error ! " + e.message, Toast.LENGTH_SHORT).show()
            }.addOnProgressListener { taskSnapshot: UploadTask.TaskSnapshot ->
                val progress = 100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount
                dialog!!.setMessage("uploaded " + progress.toInt() + "%.....")
            }
    }

    private fun uploadInfoToDB(uploadedSongUrl: String, id: String?, ref: DatabaseReference) {
        dialog!!.setMessage("Uploading song info...")
        dialog!!.show()

        //setup data to upload
        val hashMap = HashMap<String?, Any?>()
        hashMap[DATA.PUBLISHER] = DATA.EMPTY + DATA.FirebaseUserUid
        hashMap[DATA.TIMESTAMP] = System.currentTimeMillis()
        hashMap[DATA.ID] = id
        hashMap[DATA.NAME] = DATA.EMPTY + name
        hashMap[DATA.CATEGORY_ID] = DATA.EMPTY + selectedCategoryId
        hashMap[DATA.ARTIST_ID] = DATA.EMPTY + selectedArtistId
        hashMap[DATA.ALBUM_ID] = DATA.EMPTY + selectedAlbumId
        hashMap[DATA.DURATION] = DATA.EMPTY + durations
        hashMap[DATA.SONG_LINK] = DATA.EMPTY + uploadedSongUrl
        hashMap[DATA.EDITORS_CHOICE] = DATA.ZERO
        hashMap[DATA.LOVES_COUNT] = DATA.ZERO
        hashMap[DATA.VIEWS_COUNT] = DATA.ZERO
        assert(id != null)
        ref.child(id!!).setValue(hashMap).addOnSuccessListener {
            if (selectedArtistId != null) VOID.incrementItemCount(
                DATA.ARTISTS,
                selectedArtistId,
                DATA.SONGS_COUNT
            )
            if (selectedCategoryId != null) VOID.incrementItemCount(
                DATA.CATEGORIES,
                selectedCategoryId,
                DATA.SONGS_COUNT
            )
            if (selectedAlbumId != null) VOID.incrementItemCount(
                DATA.ALBUMS,
                selectedAlbumId,
                DATA.SONGS_COUNT
            )
            dialog!!.dismiss()
            Toast.makeText(activity, "Successfully uploaded...", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener { e: Exception ->
            dialog!!.dismiss()
            Toast.makeText(
                activity,
                "Failure to upload to db due to :" + e.message,
                Toast.LENGTH_SHORT
            ).show()
        }
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

    private var selectedCategoryId: String? = null
    private var selectedCategoryTitle: String? = null
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

    private var selectedAlbumId: String? = null
    private var selectedAlbumTitle: String? = null
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

    private var selectedArtistId: String? = null
    private var selectedArtistTitle: String? = null
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
}