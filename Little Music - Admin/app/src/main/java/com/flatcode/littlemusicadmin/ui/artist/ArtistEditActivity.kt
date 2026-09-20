package com.flatcode.littlemusicadmin.ui.artist

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.flatcode.littlemusicadmin.model.Artist
import com.flatcode.littlemusicadmin.R
import com.flatcode.littlemusicadmin.utils.*
import com.flatcode.littlemusicadmin.databinding.ActivityArtistAddBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback

class ArtistEditActivity : AppCompatActivity() {

    private lateinit var binding: ActivityArtistAddBinding
    var activity: Activity = this@ArtistEditActivity
    var artistId: String? = null
    private var imageUri: Uri? = null
    private var dialog: ProgressDialog? = null

    private val cropImage = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            imageUri = result.uriContent
            binding.image.setImageURI(imageUri)
        } else {
            val error = result.error
            Toast.makeText(this, "Error! $error", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityArtistAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        artistId = intent.getStringExtra(DATA.ARTIST_ID)

        dialog = ProgressDialog(activity)
        dialog!!.setTitle("Please wait...")
        dialog!!.setCanceledOnTouchOutside(false)
        loadInfo()

        binding.toolbar.nameSpace.setText(R.string.edit_artist)
        binding.toolbar.back.setOnClickListener { onBackPressed() }
        binding.image.setOnClickListener { 
            cropImage.launch(
                CropImageContractOptions(
                    uri = null,
                    cropImageOptions = CropImageOptions(
                        minCropResultWidth = DATA.MIX_SQUARE,
                        minCropResultHeight = DATA.MIX_SQUARE,
                        aspectRatioX = 1,
                        aspectRatioY = 1,
                        fixAspectRatio = true,
                        cropShape = CropImageView.CropShape.OVAL
                    )
                )
            )
        }
        binding.toolbar.ok.setOnClickListener { validateData() }
    }

    private var name = DATA.EMPTY
    private var aboutTheArtist = DATA.EMPTY
    private fun validateData() {
        name = binding.nameEt.text.toString().trim { it <= ' ' }
        aboutTheArtist = binding.aboutTheArtistEt.text.toString().trim { it <= ' ' }
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(activity, "Enter name...", Toast.LENGTH_SHORT).show()
        } else if (TextUtils.isEmpty(aboutTheArtist)) {
            Toast.makeText(activity, "Enter Description...", Toast.LENGTH_SHORT).show()
        } else {
            if (imageUri == null) {
                update(DATA.EMPTY)
            } else {
                uploadImage()
            }
        }
    }

    private fun uploadImage() {
        dialog!!.setMessage("Updating Artist...")
        dialog!!.show()
        val filePathAndName = "Images/Artists/$artistId"

        try {
            MediaManager.get().upload(imageUri)
                .unsigned(DATA.CLOUDINARY_UPLOAD_PRESET)
                .option("public_id", filePathAndName)
                .callback(object : UploadCallback {
                    override fun onStart(requestId: String) {}
                    override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {}
                    override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                        val uploadedImageUrl = resultData["secure_url"]?.toString() ?: ""
                        update(uploadedImageUrl)
                    }
                    override fun onError(requestId: String, error: ErrorInfo?) {
                        dialog!!.dismiss()
                        Toast.makeText(
                            activity, "Failed to upload image due to " + error?.description, Toast.LENGTH_SHORT
                        ).show()
                    }
                    override fun onReschedule(requestId: String, error: ErrorInfo?) {
                        dialog!!.dismiss()
                    }
                }).dispatch()
        } catch (e: Exception) {
            dialog!!.dismiss()
            Toast.makeText(activity, "Error: " + e.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun update(imageUrl: String?) {
        dialog!!.setMessage("Updating artist image...")
        dialog!!.show()
        val hashMap = HashMap<String?, Any>()
        hashMap[DATA.NAME] = DATA.EMPTY + name
        hashMap[DATA.ABOUT_THE_ARTIST] = DATA.EMPTY + aboutTheArtist
        if (imageUri != null && imageUrl!!.isNotEmpty()) {
            hashMap[DATA.IMAGE] = DATA.EMPTY + imageUrl
        }
        val reference = FirebaseDatabase.getInstance().getReference(DATA.ARTISTS)
        reference.child(artistId!!).updateChildren(hashMap).addOnSuccessListener {
            dialog!!.dismiss()
            Toast.makeText(activity, "Artist updated...", Toast.LENGTH_SHORT).show()
            finish()
        }.addOnFailureListener { e: Exception ->
            dialog!!.dismiss()
            Toast.makeText(activity, "Failed to update db duo to " + e.message, Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun loadInfo() {
        val reference = FirebaseDatabase.getInstance().getReference(DATA.ARTISTS)
        reference.child(artistId!!).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val item = snapshot.getValue(Artist::class.java)!!
                val name = item.name
                val aboutTheArtist = item.aboutTheArtist
                val image = item.image

                binding.image.glide(true, image)
                binding.nameEt.setText(name)
                binding.aboutTheArtistEt.setText(aboutTheArtist)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}