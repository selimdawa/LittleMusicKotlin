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
import com.flatcode.littlemusicadmin.utils.BaseActivity
import com.flatcode.littlemusicadmin.utils.DATA
import com.flatcode.littlemusicadmin.utils.ProgressDialog
import com.flatcode.littlemusicadmin.utils.isNetworkAvailable
import com.flatcode.littlemusicadmin.utils.startCropActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ArtistAddActivity : BaseActivity() {

    private lateinit var binding: ActivityArtistAddBinding
    var activity: Activity = this@ArtistAddActivity
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

        dialog = ProgressDialog(this).apply {
            setTitle("Please wait...")
            setCanceledOnTouchOutside(false)
        }

        binding.toolbar.nameSpace.setText(R.string.add_new_artist)
        binding.toolbar.back.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        binding.image.setOnClickListener {
            checkPermissionAndPickImage()
        }
        binding.toolbar.ok.setOnClickListener { validateData() }
    }

    private var name = DATA.EMPTY
    private var aboutTheArtist = DATA.EMPTY
    private fun validateData() {
        //get data
        name = binding.nameEt.text.toString().trim { it <= ' ' }
        aboutTheArtist = binding.aboutTheArtistEt.text.toString().trim { it <= ' ' }

        //validate data
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(activity, "Enter Name...", Toast.LENGTH_SHORT).show()
        } else if (TextUtils.isEmpty(aboutTheArtist)) {
            Toast.makeText(activity, "Enter About The Artist...", Toast.LENGTH_SHORT).show()
        } else if (imageUri == null) {
            Toast.makeText(activity, "Pick Image...", Toast.LENGTH_SHORT).show()
        } else if (!isNetworkAvailable()) {
            Toast.makeText(activity, getString(R.string.no_internet_connection), Toast.LENGTH_SHORT)
                .show()
        } else {
            uploadToStorage()
        }
    }

    private fun uploadToStorage() {
        dialog?.setMessage("Uploading Artist...")
        dialog?.show()
        val ref = FirebaseDatabase.getInstance().getReference(DATA.ARTISTS)
        val id = ref.push().key
        val filePathAndName = "Images/Artists/$id"

        try {
            MediaManager.get().upload(imageUri).unsigned(DATA.CLOUDINARY_UPLOAD_PRESET)
                .option("public_id", filePathAndName).callback(object : UploadCallback {
                    override fun onStart(requestId: String) {}
                    override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {}
                    override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                        val uploadedImageUrl = resultData["secure_url"]?.toString() ?: ""
                        uploadInfoDB(uploadedImageUrl, id, ref)
                    }

                    override fun onError(requestId: String, error: ErrorInfo?) {
                        dialog?.dismiss()
                        Toast.makeText(
                            activity,
                            "Artist upload failed due to : " + error?.description,
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

    private fun uploadInfoDB(uploadedImageUrl: String, id: String?, ref: DatabaseReference) {
        dialog?.setMessage("Uploading Artist info...")
        dialog?.show()

        //setup data to upload
        val hashMap = HashMap<String?, Any?>()
        hashMap[DATA.PUBLISHER] = DATA.EMPTY + DATA.FirebaseUserUid
        hashMap[DATA.TIMESTAMP] = System.currentTimeMillis()
        hashMap[DATA.ID] = id
        hashMap[DATA.NAME] = DATA.EMPTY + name
        hashMap[DATA.ABOUT_THE_ARTIST] = DATA.EMPTY + aboutTheArtist
        hashMap[DATA.IMAGE] = uploadedImageUrl
        hashMap[DATA.INTERESTED_COUNT] = DATA.ZERO
        hashMap[DATA.SONGS_COUNT] = DATA.ZERO
        hashMap[DATA.ALBUMS_COUNT] = DATA.ZERO
        assert(id != null)
        ref.child(id!!).setValue(hashMap).addOnSuccessListener {
            dialog?.dismiss()
            Toast.makeText(activity, "Successfully uploaded...", Toast.LENGTH_SHORT).show()
            finish()
        }.addOnFailureListener { e: Exception ->
            dialog?.dismiss()
            Toast.makeText(
                activity, "Failure to upload to db due to : " + e.message, Toast.LENGTH_SHORT
            ).show()
        }
    }
}