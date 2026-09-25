package com.flatcode.littlemusicadmin.ui.song

import android.app.Activity
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import com.flatcode.littlemusicadmin.utils.BaseActivity
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.flatcode.littlemusicadmin.R
import com.flatcode.littlemusicadmin.databinding.ActivitySongAddBinding
import com.flatcode.littlemusicadmin.utils.DATA
import com.flatcode.littlemusicadmin.utils.convertDuration
import com.flatcode.littlemusicadmin.utils.incrementItemCount
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SongAddActivity : BaseActivity() {
    private lateinit var binding: ActivitySongAddBinding
    var activity: Activity = this@SongAddActivity
    var audioUri: Uri? = null
    var metadataRetriever: MediaMetadataRetriever? = null

    //byte[] art;
    var nameSong: String? = null
    var durations //album_art = "",;
            : String? = null
    private var dialog: AlertDialog? = null
    private var categoryId: ArrayList<String>? = null
    private var categoryList: ArrayList<String>? = null
    private var albumId: ArrayList<String>? = null
    private var albumList: ArrayList<String>? = null
    private var artistId: ArrayList<String>? = null
    private var artistList: ArrayList<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySongAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dialog = AlertDialog.Builder(this).apply {
            setTitle("Please wait...")
            setCancelable(false)
        }.create()

        loadCategories()
        loadAlbums()
        loadArtists()

        binding.toolbar.nameSpace.setText(R.string.add_new_song)
        binding.toolbar.back.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        binding.category.setOnClickListener { categoryPickDialog() }
        binding.album.setOnClickListener { albumPickDialog() }
        binding.artist.setOnClickListener { artistPickDialog() }
        binding.chooseSong.setOnClickListener { openAudioFiles() }
        binding.toolbar.ok.setOnClickListener { validateData() }
        metadataRetriever = MediaMetadataRetriever()
    }

    private var name = DATA.EMPTY
    private fun validateData() {
        //get data
        name = binding.nameEt.text.toString().trim { it <= ' ' }

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
        val intentUpload = Intent()
        intentUpload.type = "audio/*"
        intentUpload.action = Intent.ACTION_GET_CONTENT
        pickAudioLauncher.launch(intentUpload)
    }

    private val pickAudioLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                if (data?.data != null) {
                    try {
                        audioUri = data.data
                        metadataRetriever!!.setDataSource(this, audioUri)
                        assert(metadataRetriever != null)
                        nameSong =
                            metadataRetriever!!.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
                        durations =
                            metadataRetriever!!.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                        binding.choose.setText(R.string.ok)
                        name = binding.nameEt.text.toString().trim { it <= ' ' }
                        if (TextUtils.isEmpty(name)) binding.nameEt.setText(nameSong)
                        binding.duration.text = durations!!.toLong().convertDuration()
                        name = nameSong!!
                    } catch (_: Exception) {
                        Toast.makeText(activity, "Error!", Toast.LENGTH_SHORT).show()
                        audioUri = null
                    }
                } else {
                    Toast.makeText(this, "Error ! ", Toast.LENGTH_SHORT).show()
                    audioUri = null
                }
            }
        }

    fun uploadFileToDB() {
        val message = binding.choose.text.toString()
        if (message == "No file Selected") {
            Toast.makeText(this, "Please selected an image!", Toast.LENGTH_SHORT).show()
        } else {
            uploadFile()
        }
    }

    private fun uploadFile() {
        Toast.makeText(this, "Uploads please wait!", Toast.LENGTH_SHORT).show()
        dialog!!.setMessage("Uploads Song...")
        dialog!!.show()
        val ref = FirebaseDatabase.getInstance().getReference(DATA.SONGS)
        val id = ref.push().key
        val filePathAndName = "Songs/$selectedArtistTitle/$id"

        try {
            MediaManager.get().upload(audioUri).unsigned(DATA.CLOUDINARY_UPLOAD_PRESET)
                .option("public_id", filePathAndName)
                .option("resource_type", "video") // video handles audio files in Cloudinary
                .callback(object : UploadCallback {
                    override fun onStart(requestId: String) {}
                    override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {
                        val progress = 100.0 * bytes / totalBytes
                        dialog!!.setMessage("uploaded " + progress.toInt() + "%.....")
                    }

                    override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                        val uploadedSongUrl = resultData["secure_url"]?.toString() ?: ""
                        dialog!!.dismiss()
                        Toast.makeText(this@SongAddActivity, "Ok", Toast.LENGTH_SHORT).show()
                        uploadInfoToDB(uploadedSongUrl, id, ref)
                    }

                    override fun onError(requestId: String, error: ErrorInfo?) {
                        dialog!!.dismiss()
                        Toast.makeText(
                            this@SongAddActivity,
                            "Error ! " + error?.description,
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    override fun onReschedule(requestId: String, error: ErrorInfo?) {
                        dialog!!.dismiss()
                    }
                }).dispatch()
        } catch (e: Exception) {
            dialog!!.dismiss()
            Toast.makeText(this, "Error ! " + e.message, Toast.LENGTH_SHORT).show()
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
            if (selectedArtistId != null) selectedArtistId!!.incrementItemCount(
                DATA.ARTISTS, DATA.SONGS_COUNT
            )
            if (selectedCategoryId != null) selectedCategoryId!!.incrementItemCount(
                DATA.CATEGORIES, DATA.SONGS_COUNT
            )
            if (selectedAlbumId != null) selectedAlbumId!!.incrementItemCount(
                DATA.ALBUMS, DATA.SONGS_COUNT
            )
            dialog!!.dismiss()
            Toast.makeText(activity, "Successfully uploaded...", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener { e: Exception ->
            dialog!!.dismiss()
            Toast.makeText(
                activity, "Failure to upload to db due to :" + e.message, Toast.LENGTH_SHORT
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
        builder.setTitle("Pick Category").setItems(categories) { _, which ->
            selectedCategoryTitle = categoryList!![which]
            selectedCategoryId = categoryId!![which]
            binding.category.text = selectedCategoryTitle
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
        builder.setTitle("Pick Album").setItems(albums) { _, which ->
            selectedAlbumTitle = albumList!![which]
            selectedAlbumId = albumId!![which]
            binding.album.text = selectedAlbumTitle
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
        builder.setTitle("Pick Artist").setItems(artists) { _, which ->
            selectedArtistTitle = artistList!![which]
            selectedArtistId = artistId!![which]
            binding.artist.text = selectedArtistTitle
        }.show()
    }
}