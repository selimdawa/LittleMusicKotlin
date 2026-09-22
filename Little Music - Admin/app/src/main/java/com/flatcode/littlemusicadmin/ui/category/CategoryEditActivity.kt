package com.flatcode.littlemusicadmin.ui.category

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.flatcode.littlemusicadmin.model.Category
import com.flatcode.littlemusicadmin.R
import com.flatcode.littlemusicadmin.utils.*
import com.flatcode.littlemusicadmin.databinding.ActivityCategoryAddBinding
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

class CategoryEditActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCategoryAddBinding
    var activity: Activity = this@CategoryEditActivity
    var categoryId: String? = null
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
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        categoryId = intent.getStringExtra(DATA.CATEGORY_ID)
        dialog = AlertDialog.Builder(activity).apply {
            setTitle("Please wait...")
            setCancelable(false)
        }.create()
        loadCategoryInfo()

        binding.toolbar.nameSpace.setText(R.string.edit_category)
        binding.toolbar.back.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
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
    private fun validateData() {
        name = binding.nameEt.text.toString().trim { it <= ' ' }
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(activity, "Enter name...", Toast.LENGTH_SHORT).show()
        } else {
            if (imageUri == null) {
                updateCategory(DATA.EMPTY)
            } else {
                uploadImage()
            }
        }
    }

    private fun uploadImage() {
        dialog!!.setMessage("Updating Category...")
        dialog!!.show()
        val filePathAndName = "Images/Category/$categoryId"

        try {
            MediaManager.get().upload(imageUri)
                .unsigned(DATA.CLOUDINARY_UPLOAD_PRESET)
                .option("public_id", filePathAndName)
                .callback(object : UploadCallback {
                    override fun onStart(requestId: String) {}
                    override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {}
                    override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                        val uploadedImageUrl = resultData["secure_url"]?.toString() ?: ""
                        updateCategory(uploadedImageUrl)
                    }
                    override fun onError(requestId: String, error: ErrorInfo?) {
                        dialog!!.dismiss()
                        Toast.makeText(
                            activity, "Failed to upload image due to : " + error?.description, Toast.LENGTH_SHORT
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

    private fun updateCategory(imageUrl: String?) {
        dialog!!.setMessage("Updating category image...")
        dialog!!.show()
        val hashMap = HashMap<String?, Any>()
        hashMap[DATA.NAME] = DATA.EMPTY + name
        if (imageUri != null && imageUrl!!.isNotEmpty()) {
            hashMap[DATA.IMAGE] = DATA.EMPTY + imageUrl
        }
        val reference = FirebaseDatabase.getInstance().getReference(DATA.CATEGORIES)
        reference.child(categoryId!!).updateChildren(hashMap)
            .addOnSuccessListener {
                dialog!!.dismiss()
                Toast.makeText(activity, "Category updated...", Toast.LENGTH_SHORT).show()
                finish()
            }.addOnFailureListener { e: Exception ->
                dialog!!.dismiss()
                Toast.makeText(
                    activity, "Failed to update db duo to : " + e.message, Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun loadCategoryInfo() {
        val reference = FirebaseDatabase.getInstance().getReference(DATA.CATEGORIES)
        reference.child(categoryId!!).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val item = snapshot.getValue(Category::class.java)!!
                val name = item.name
                val image = item.image

                binding.image.loadImage(true, image)
                binding.nameEt.setText(name)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}
