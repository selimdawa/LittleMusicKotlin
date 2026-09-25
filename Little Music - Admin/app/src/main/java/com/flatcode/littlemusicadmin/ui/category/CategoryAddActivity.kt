package com.flatcode.littlemusicadmin.ui.category

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.flatcode.littlemusicadmin.utils.BaseActivity
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.flatcode.littlemusicadmin.R
import com.flatcode.littlemusicadmin.databinding.ActivityCategoryAddBinding
import com.flatcode.littlemusicadmin.utils.DATA
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class CategoryAddActivity : BaseActivity() {

    private lateinit var binding: ActivityCategoryAddBinding
    var activity: Activity? = null
    var context: Context = also { activity = it }
    private var imageUri: Uri? = null
    private var dialog: AlertDialog? = null

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
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dialog = AlertDialog.Builder(context).apply {
            setTitle("Please wait...")
            setCancelable(false)
        }.create()

        binding.toolbar.nameSpace.setText(R.string.add_new_category)
        binding.toolbar.back.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        binding.image.setOnClickListener {
            cropImage.launch(
                CropImageContractOptions(
                    uri = null, cropImageOptions = CropImageOptions(
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
    private fun validateData() {
        //get data
        name = binding.nameEt.text.toString().trim { it <= ' ' }

        //validate data
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(context, "Enter Name...", Toast.LENGTH_SHORT).show()
        } else if (imageUri == null) {
            Toast.makeText(context, "Pick Image...", Toast.LENGTH_SHORT).show()
        } else {
            uploadToStorage()
        }
    }

    private fun uploadToStorage() {
        dialog!!.setMessage("Uploading Category...")
        dialog!!.show()
        val ref = FirebaseDatabase.getInstance().getReference(DATA.CATEGORIES)
        val id = ref.push().key
        val filePathAndName = "Images/Category/$id"

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
                        dialog!!.dismiss()
                        Toast.makeText(
                            context,
                            "Category upload failed due to : " + error?.description,
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    override fun onReschedule(requestId: String, error: ErrorInfo?) {
                        dialog!!.dismiss()
                    }
                }).dispatch()
        } catch (e: Exception) {
            dialog!!.dismiss()
            Toast.makeText(context, "Error: " + e.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadInfoDB(uploadedImageUrl: String, id: String?, ref: DatabaseReference) {
        dialog!!.setMessage("Uploading Category info...")
        dialog!!.show()

        //setup data to upload
        val hashMap = HashMap<String?, Any?>()
        hashMap[DATA.PUBLISHER] = DATA.EMPTY + DATA.FirebaseUserUid
        hashMap[DATA.TIMESTAMP] = System.currentTimeMillis()
        hashMap[DATA.ID] = id
        hashMap[DATA.NAME] = DATA.EMPTY + name
        hashMap[DATA.IMAGE] = uploadedImageUrl
        hashMap[DATA.INTERESTED_COUNT] = DATA.ZERO
        hashMap[DATA.SONGS_COUNT] = DATA.ZERO
        hashMap[DATA.ALBUMS_COUNT] = DATA.ZERO
        assert(id != null)
        ref.child(id!!).setValue(hashMap).addOnSuccessListener {
            dialog!!.dismiss()
            Toast.makeText(context, "Successfully uploaded...", Toast.LENGTH_SHORT).show()
            finish()
        }.addOnFailureListener { e: Exception ->
            dialog!!.dismiss()
            Toast.makeText(
                context, "Failure to upload to db due to : " + e.message, Toast.LENGTH_SHORT
            ).show()
        }
    }
}
