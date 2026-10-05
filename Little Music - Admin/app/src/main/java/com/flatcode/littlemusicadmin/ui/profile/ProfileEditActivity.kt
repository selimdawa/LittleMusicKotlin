package com.flatcode.littlemusicadmin.ui.profile

import android.Manifest
import android.app.Activity
import android.content.Context
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
import com.flatcode.littlemusicadmin.databinding.ActivityProfileEditBinding
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
import java.util.Objects

class ProfileEditActivity : BaseActivity() {

    private lateinit var binding: ActivityProfileEditBinding
    var activity: Activity? = null
    var context: Context = also { activity = it }
    private var imageUri: Uri? = null
    private var dialog: ProgressDialog? = null

    private val cropImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                imageUri = result.data?.let { intent ->
                    IntentCompat.getParcelableExtra(intent, "CROP_RESULT_URI", Uri::class.java)
                }
                binding.profileImage.setImageURI(null)
                binding.profileImage.setImageURI(imageUri)
            }
        }

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                cropImageLauncher.launch(context.startCropActivity(it, 1, 1, true))
            }
        }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                pickImageLauncher.launch("image/*")
            } else {
                Toast.makeText(context, "Permission denied...", Toast.LENGTH_SHORT).show()
            }
        }

    private fun checkPermissionAndPickImage() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (ContextCompat.checkSelfPermission(
                context, permission
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            pickImageLauncher.launch("image/*")
        } else {
            requestPermissionLauncher.launch(permission)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dialog = ProgressDialog(this).apply {
            setTitle("Please wait...")
            setCanceledOnTouchOutside(false)
        }

        loadUserInfo()
        binding.toolbar.nameSpace.setText(R.string.edit_profile)
        binding.toolbar.back.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        binding.image.setOnClickListener {
            checkPermissionAndPickImage()
        }
        binding.go.setOnClickListener { validateData() }
    }

    private var username = DATA.EMPTY
    private fun validateData() {
        username = binding.nameEt.text.toString().trim { it <= ' ' }
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(context, "Enter name...", Toast.LENGTH_SHORT).show()
        } else if (!isNetworkAvailable()) {
            Toast.makeText(context, getString(R.string.no_internet_connection), Toast.LENGTH_SHORT)
                .show()
        } else {
            if (imageUri == null) {
                updateProfile(DATA.EMPTY)
            } else {
                uploadImage()
            }
        }
    }

    private fun uploadImage() {
        dialog?.setMessage("Uploading Image...")
        dialog?.show()
        val filePathAndName = "Images/Profile/" + DATA.FirebaseUserUid

        try {
            MediaManager.get().upload(imageUri).unsigned(DATA.CLOUDINARY_UPLOAD_PRESET)
                .option("public_id", filePathAndName).callback(object : UploadCallback {
                    override fun onStart(requestId: String) {}
                    override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {}
                    override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                        val uploadedImageUrl = resultData["secure_url"]?.toString() ?: ""
                        updateProfile(uploadedImageUrl)
                    }

                    override fun onError(requestId: String, error: ErrorInfo?) {
                        dialog?.dismiss()
                        Toast.makeText(
                            context,
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
            Toast.makeText(context, "Error: " + e.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateProfile(imageUrl: String?) {
        dialog?.setMessage("Updating user profile...")
        dialog?.show()
        val hashMap = HashMap<String?, Any>()
        hashMap[DATA.USER_NAME] = DATA.EMPTY + username
        if (imageUri != null && !imageUrl.isNullOrEmpty()) {
            hashMap[DATA.PROFILE_IMAGE] = DATA.EMPTY + imageUrl
        }
        val reference = FirebaseDatabase.getInstance().getReference(DATA.USERS)
        reference.child(Objects.requireNonNull(DATA.FirebaseUserUid)).updateChildren(hashMap)
            .addOnSuccessListener {
                dialog?.dismiss()
                Toast.makeText(context, "Profile updated...", Toast.LENGTH_SHORT).show()
                finish()
            }.addOnFailureListener { e: Exception ->
                dialog?.dismiss()
                Toast.makeText(
                    context, "Failed to update db duo to " + e.message, Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun loadUserInfo() {
        val reference = FirebaseDatabase.getInstance().getReference(DATA.USERS)
        reference.child(Objects.requireNonNull(DATA.FirebaseUserUid))
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val username = DATA.EMPTY + snapshot.child(DATA.USER_NAME).value
                    val profileImage = DATA.EMPTY + snapshot.child(DATA.PROFILE_IMAGE).value
                    binding.profileImage.loadImage(true, profileImage)
                    binding.nameEt.setText(username)
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }
}