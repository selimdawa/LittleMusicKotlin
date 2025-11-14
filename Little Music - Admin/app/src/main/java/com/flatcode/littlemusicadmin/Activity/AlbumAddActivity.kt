package com.flatcode.littlemusicadmin.Activity

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
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
import com.flatcode.littlemusicadmin.databinding.ActivityAlbumAddBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.theartofdev.edmodo.cropper.CropImage

class AlbumAddActivity : AppCompatActivity() {

    private var binding: ActivityAlbumAddBinding? = null
    var activity: Activity? = null
    var context: Context = also { activity = it }
    private var imageUri: Uri? = null
    private var dialog: ProgressDialog? = null
    private var categoryId: ArrayList<String>? = null
    private var categoryList: ArrayList<String>? = null
    private var artistId: ArrayList<String>? = null
    private var artistList: ArrayList<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        THEME.setThemeOfApp(context)
        super.onCreate(savedInstanceState)
        binding = ActivityAlbumAddBinding.inflate(layoutInflater)
        val view = binding!!.root
        setContentView(view)

        dialog = ProgressDialog(context)
        dialog!!.setTitle("Please wait...")
        dialog!!.setCanceledOnTouchOutside(false)
        loadCategories()
        loadArtists()

        binding!!.toolbar.nameSpace.setText(R.string.add_new_album)
        binding!!.toolbar.back.setOnClickListener { onBackPressed() }
        binding!!.image.setOnClickListener { VOID.CropImageSquare(activity) }
        binding!!.category.setOnClickListener { categoryPickDialog() }
        binding!!.artist.setOnClickListener { artistPickDialog() }
        binding!!.toolbar.ok.setOnClickListener { validateData() }
    }

    private var name = DATA.EMPTY
    private fun validateData() {
        //get data
        name = binding!!.nameEt.text.toString().trim { it <= ' ' }

        //validate data
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(context, "Enter Name...", Toast.LENGTH_SHORT).show()
        } else if (TextUtils.isEmpty(selectedArtistId)) {
            Toast.makeText(context, "Enter Artist...", Toast.LENGTH_SHORT).show()
        } else if (TextUtils.isEmpty(selectedCategoryId)) {
            Toast.makeText(context, "Enter Category...", Toast.LENGTH_SHORT).show()
        } else if (imageUri == null) {
            Toast.makeText(context, "Pick Image...", Toast.LENGTH_SHORT).show()
        } else {
            uploadToStorage()
        }
    }

    private fun uploadToStorage() {
        dialog!!.setMessage("Uploading Album...")
        dialog!!.show()
        val ref = FirebaseDatabase.getInstance().getReference(DATA.ALBUMS)
        val id = ref.push().key
        val filePathAndName = "Images/Album/$id"
        val reference = FirebaseStorage.getInstance()
            .getReference(filePathAndName + DATA.DOT + VOID.getFileExtension(imageUri, context))
        reference.putFile(imageUri!!)
            .addOnSuccessListener { taskSnapshot: UploadTask.TaskSnapshot ->
                val uriTask = taskSnapshot.storage.downloadUrl
                while (!uriTask.isSuccessful);
                val uploadedImageUrl = DATA.EMPTY + uriTask.result
                uploadInfoToDB(uploadedImageUrl, id, ref)
            }.addOnFailureListener { e: Exception ->
                dialog!!.dismiss()
                Toast.makeText(
                    context, "Category upload failed due to " + e.message, Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun uploadInfoToDB(uploadedImageUrl: String, id: String?, ref: DatabaseReference) {
        dialog!!.setMessage("Uploading category info...")
        dialog!!.show()

        //setup data to upload
        val hashMap = HashMap<String?, Any?>()
        hashMap[DATA.PUBLISHER] = DATA.EMPTY + DATA.FirebaseUserUid
        hashMap[DATA.TIMESTAMP] = System.currentTimeMillis()
        hashMap[DATA.ID] = id
        hashMap[DATA.NAME] = DATA.EMPTY + name
        hashMap[DATA.CATEGORY_ID] = DATA.EMPTY + selectedCategoryId
        hashMap[DATA.ARTIST_ID] = DATA.EMPTY + selectedArtistId
        hashMap[DATA.IMAGE] = uploadedImageUrl
        hashMap[DATA.INTERESTED_COUNT] = DATA.ZERO
        hashMap[DATA.SONGS_COUNT] = DATA.ZERO
        assert(id != null)
        ref.child(id!!).setValue(hashMap).addOnSuccessListener {
            if (selectedArtistId != null) VOID.incrementItemCount(
                DATA.ARTISTS, selectedArtistId, DATA.ALBUMS_COUNT
            )
            if (selectedCategoryId != null) VOID.incrementItemCount(
                DATA.CATEGORIES, selectedCategoryId, DATA.ALBUMS_COUNT
            )
            dialog!!.dismiss()
            Toast.makeText(context, "Successfully uploaded...", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener { e: Exception ->
            dialog!!.dismiss()
            Toast.makeText(
                context, "Failure to upload to db due to :" + e.message, Toast.LENGTH_SHORT
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
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Pick Category")
            .setItems(categories) { dialog: DialogInterface?, which: Int ->
                selectedCategoryTitle = categoryList!![which]
                selectedCategoryId = categoryId!![which]
                binding!!.category.text = selectedCategoryTitle
            }.show()
    }

    private var selectedArtistId: String? = null
    private var selectedArtistTitle: String? = null
    private fun artistPickDialog() {
        val artists = arrayOfNulls<String>(artistList!!.size)
        for (i in artistList!!.indices) {
            artists[i] = artistList!![i]
        }
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Pick Artist").setItems(artists) { dialog: DialogInterface?, which: Int ->
            selectedArtistTitle = artistList!![which]
            selectedArtistId = artistId!![which]
            binding!!.artist.text = selectedArtistTitle
        }.show()
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == RESULT_OK) {
            val uri = CropImage.getPickImageResultUri(context, data)
            if (CropImage.isReadExternalStoragePermissionsRequired(context, uri)) {
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