package com.flatcode.littlemusicadmin.ui.artist

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.IntentCompat
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.flatcode.littlemusicadmin.R
import com.flatcode.littlemusicadmin.databinding.ActivityArtistAddBinding
import com.flatcode.littlemusicadmin.model.Artist
import com.flatcode.littlemusicadmin.utils.BaseActivity
import com.flatcode.littlemusicadmin.utils.DATA
import com.flatcode.littlemusicadmin.utils.ProgressDialog
import com.flatcode.littlemusicadmin.utils.isNetworkAvailable
import com.flatcode.littlemusicadmin.utils.loadImage
import com.flatcode.littlemusicadmin.utils.startCropActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ArtistEditActivity : BaseActivity() {

    private lateinit var binding: ActivityArtistAddBinding
    var activity: Activity = this@ArtistEditActivity
    var artistId: String? = null
    private var imageUri: Uri? = null
    private var dialog: ProgressDialog? = null

    private val cropImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                imageUri = result.data?.let { intent ->
                    IntentCompat.getParcelableExtra(intent, "CROP_RESULT_URI", Uri::class.java)
                }
                binding.image.setImageURI(null)
                binding.image.setImageURI(imageUri)
            }
        }

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                cropImageLauncher.launch(activity.startCropActivity(it, 1, 1, false))
            }
        }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                pickImageLauncher.launch("image/*")
            } else {
                Toast.makeText(activity, "Permission denied...", Toast.LENGTH_SHORT).show()
            }
        }

    private fun checkPermissionAndPickImage() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (ContextCompat.checkSelfPermission(
                activity, permission
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            pickImageLauncher.launch("image/*")
        } else {
            requestPermissionLauncher.launch(permission)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityArtistAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        artistId = intent.getStringExtra(DATA.ARTIST_ID)

        dialog = ProgressDialog(this).apply {
            setTitle("Please wait...")
            setCanceledOnTouchOutside(false)
        }
        loadInfo()

        binding.toolbar.nameSpace.setText(R.string.edit_artist)
        binding.toolbar.back.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        binding.image.setOnClickListener {
            checkPermissionAndPickImage()
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
        } else if (!isNetworkAvailable()) {
            Toast.makeText(activity, getString(R.string.no_internet_connection), Toast.LENGTH_SHORT)
                .show()
        } else {
            if (imageUri == null) {
                update(DATA.EMPTY)
            } else {
                uploadImage()
            }
        }
    }

    private fun uploadImage() {
        dialog?.setMessage("Updating Artist...")
        dialog?.show()
        val filePathAndName = "Images/Artists/$artistId"

        try {
            MediaManager.get().upload(imageUri).unsigned(DATA.CLOUDINARY_UPLOAD_PRESET)
                .option("public_id", filePathAndName).callback(object : UploadCallback {
                    override fun onStart(requestId: String) {}
                    override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {}
                    override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                        val uploadedImageUrl = resultData["secure_url"]?.toString() ?: ""
                        update(uploadedImageUrl)
                    }

                    override fun onError(requestId: String, error: ErrorInfo?) {
                        dialog?.dismiss()
                        Toast.makeText(
                            activity,
                            "Failed to upload image due to " + error?.description,
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    override fun onReschedule(requestId: String, error: ErrorInfo?) {
                        dialog?.dismiss()
                    }
                }).dispatch()
        } catch (e: Exception) {
            dialog?.dismiss()
            Toast.makeText(activity, "Error: " + e.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun update(imageUrl: String?) {
        dialog?.setMessage("Updating artist image...")
        dialog?.show()
        val hashMap = HashMap<String?, Any>()
        hashMap[DATA.NAME] = DATA.EMPTY + name
        hashMap[DATA.ABOUT_THE_ARTIST] = DATA.EMPTY + aboutTheArtist
        if (imageUri != null && !imageUrl.isNullOrEmpty()) {
            hashMap[DATA.IMAGE] = DATA.EMPTY + imageUrl
        }
        val reference = FirebaseDatabase.getInstance().getReference(DATA.ARTISTS)
        reference.child(artistId!!).updateChildren(hashMap).addOnSuccessListener {
            dialog?.dismiss()
            Toast.makeText(activity, "Artist updated...", Toast.LENGTH_SHORT).show()
            finish()
        }.addOnFailureListener { e: Exception ->
            dialog?.dismiss()
            Toast.makeText(activity, "Failed to update db duo to " + e.message, Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun loadInfo() {
        val reference = FirebaseDatabase.getInstance().getReference(DATA.ARTISTS)
        reference.child(artistId!!).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val item = snapshot.getValue(Artist::class.java) ?: return
                val name = item.name
                val aboutTheArtist = item.aboutTheArtist
                val image = item.image

                binding.image.loadImage(true, image)
                binding.nameEt.setText(name)
                binding.aboutTheArtistEt.setText(aboutTheArtist)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}