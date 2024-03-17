package com.flatcode.littlemusicadmin.Activity

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.flatcode.littlemusicadmin.Model.Album
import com.flatcode.littlemusicadmin.R
import com.flatcode.littlemusicadmin.Unit.DATA
import com.flatcode.littlemusicadmin.Unit.THEME
import com.flatcode.littlemusicadmin.Unit.VOID
import com.flatcode.littlemusicadmin.databinding.ActivityAlbumAddBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.theartofdev.edmodo.cropper.CropImage

class AlbumEditActivity : AppCompatActivity() {

    private var binding: ActivityAlbumAddBinding? = null
    var activity: Activity = this@AlbumEditActivity
    private var albumId: String? = null
    private var category: String? = null
    private var artist: String? = null
    private var categoryList: ArrayList<String>? = null
    private var categoryId: ArrayList<String>? = null
    private var artistList: ArrayList<String>? = null
    private var artistId: ArrayList<String>? = null
    private var imageUri: Uri? = null
    private var dialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        THEME.setThemeOfApp(activity)
        super.onCreate(savedInstanceState)
        binding = ActivityAlbumAddBinding.inflate(layoutInflater)
        val view = binding!!.root
        setContentView(view)

        albumId = intent.getStringExtra(DATA.ALBUM_ID)
        category = intent.getStringExtra(DATA.CATEGORY_ID)
        artist = intent.getStringExtra(DATA.ARTIST_ID)

        dialog = ProgressDialog(activity)
        dialog!!.setTitle("Please wait...")
        dialog!!.setCanceledOnTouchOutside(false)

        loadCategories()
        loadArtists()
        loadInfo()

        binding!!.toolbar.nameSpace.setText(R.string.edit_album)
        binding!!.toolbar.back.setOnClickListener { onBackPressed() }
        binding!!.category.setOnClickListener { categoryPickDialog() }
        binding!!.artist.setOnClickListener { artistPickDialog() }
        binding!!.image.setOnClickListener { VOID.CropImageSquare(activity) }
        binding!!.toolbar.ok.setOnClickListener { validateData() }
    }

    private var name = DATA.EMPTY
    private var selectedCategoryId: String? = null
    private var selectedCategoryTitle: String? = null
    private var selectedArtistId: String? = null
    private var selectedArtistTitle: String? = null
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
        ).show() else update()
    }

    private fun update() {
        dialog!!.setMessage("Updating Album image...")
        dialog!!.show()
        val hashMap = HashMap<String?, Any>()
        hashMap[DATA.NAME] = DATA.EMPTY + name
        hashMap[DATA.CATEGORY_ID] = DATA.EMPTY + selectedCategoryId
        hashMap[DATA.ARTIST_ID] = DATA.EMPTY + selectedArtistId
        val reference = FirebaseDatabase.getInstance().getReference(DATA.ALBUMS)
        reference.child(albumId!!).updateChildren(hashMap).addOnSuccessListener {
            dialog!!.dismiss()
            Toast.makeText(activity, "Album updated...", Toast.LENGTH_SHORT).show()
        }.addOnCompleteListener {
            if (selectedCategoryId != category) {
                VOID.incrementItemCount(DATA.CATEGORIES, selectedCategoryId, DATA.ALBUMS_COUNT)
                if (category != null) VOID.incrementItemRemoveCount(
                    DATA.CATEGORIES, category, DATA.ALBUMS_COUNT
                )
            }
            if (selectedArtistId != artist) {
                VOID.incrementItemCount(DATA.ARTISTS, selectedArtistId, DATA.ALBUMS_COUNT)
                if (artist != null) VOID.incrementItemRemoveCount(
                    DATA.ARTISTS, artist, DATA.ALBUMS_COUNT
                )
            }
            if (imageUri != null) {
                uploadImage()
            } else {
                finish()
            }
        }.addOnFailureListener { e: Exception ->
            dialog!!.dismiss()
            Toast.makeText(
                activity, "Failed to update db duo to : " + e.message, Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun uploadImage() {
        dialog!!.setMessage("Updating Album...")
        dialog!!.show()
        val filePathAndName = "Images/Album/$albumId"
        val reference = FirebaseStorage.getInstance().getReference(
            filePathAndName + DATA.DOT + VOID.getFileExtension(imageUri, activity)
        )
        reference.putFile(imageUri!!)
            .addOnSuccessListener { taskSnapshot: UploadTask.TaskSnapshot ->
                val uriTask = taskSnapshot.storage.downloadUrl
                while (!uriTask.isSuccessful);
                val uploadedImageUrl = DATA.EMPTY + uriTask.result
                updateImageAlbum(uploadedImageUrl, albumId)
            }.addOnFailureListener { e: Exception ->
                dialog!!.dismiss()
                Toast.makeText(
                    activity, "Failed to upload image due to : " + e.message, Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun updateImageAlbum(imageUrl: String, id: String?) {
        dialog!!.setMessage("Updating image album...")
        dialog!!.show()
        val hashMap = HashMap<String?, Any>()
        if (imageUri != null) {
            hashMap[DATA.IMAGE] = DATA.EMPTY + imageUrl
        }
        val reference = FirebaseDatabase.getInstance().getReference(DATA.ALBUMS)
        reference.child(id!!).updateChildren(hashMap).addOnSuccessListener {
            dialog!!.dismiss()
            Toast.makeText(activity, "Image updated...", Toast.LENGTH_SHORT).show()
            finish()
        }.addOnFailureListener { e: Exception ->
            dialog!!.dismiss()
            Toast.makeText(activity, "Failed to update db duo to " + e.message, Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun loadInfo() {
        val reference = FirebaseDatabase.getInstance().getReference(DATA.ALBUMS)
        reference.child(albumId!!).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val item = snapshot.getValue(Album::class.java)!!
                selectedCategoryId = DATA.EMPTY + snapshot.child(DATA.CATEGORY_ID).value
                selectedArtistId = DATA.EMPTY + snapshot.child(DATA.ARTIST_ID).value

                val name = item.name
                val image = item.image
                VOID.Glide(true, activity, image, binding!!.image)
                binding!!.nameEt.setText(name)

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

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == RESULT_OK) {
            val uri = CropImage.getPickImageResultUri(activity, data)
            if (CropImage.isReadExternalStoragePermissionsRequired(activity, uri)) {
                imageUri = uri
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 0)
            } else {
                VOID.CropImageSquare(activity)
            }
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                imageUri = result.uri
                binding!!.image.setImageURI(imageUri)
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
                Toast.makeText(this, "Error! $error", Toast.LENGTH_SHORT).show()
            }
        }
    }
}