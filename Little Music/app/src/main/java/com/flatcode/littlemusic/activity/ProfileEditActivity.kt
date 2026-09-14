package com.flatcode.littlemusic.activity

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.flatcode.littlemusic.R
import com.flatcode.littlemusic.utils.VOID
import com.flatcode.littlemusic.utils.DATA
import com.flatcode.littlemusic.databinding.ActivityProfileEditBinding
import com.flatcode.littlemusic.viewmodel.ProfileEditViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import com.theartofdev.edmodo.cropper.CropImage

@AndroidEntryPoint
class ProfileEditActivity : AppCompatActivity() {

    private var binding: ActivityProfileEditBinding? = null
    private val viewModel: ProfileEditViewModel by viewModels()
    private var imageUri: Uri? = null
    private var dialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileEditBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        Timber.i("ProfileEditActivity Created")

        dialog = ProgressDialog(this)
        dialog!!.setTitle("Please wait...")
        dialog!!.setCanceledOnTouchOutside(false)

        binding!!.toolbar.nameSpace.setText(R.string.edit_profile)
        binding!!.toolbar.back.setOnClickListener { onBackPressed() }
        binding!!.image.setOnClickListener { VOID.CropImageSquare(this) }
        binding!!.go.setOnClickListener {
            viewModel.updateProfile(binding!!.nameEt.text.toString().trim(), imageUri, this)
        }

        observeViewModel()
        viewModel.loadUserInfo()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.username.collect { username ->
                        binding!!.nameEt.setText(username)
                    }
                }
                launch {
                    viewModel.profileImage.collect { profileImage ->
                        VOID.GlideImage(true, this@ProfileEditActivity, profileImage, binding!!.profileImage)
                    }
                }
                launch {
                    viewModel.updateStatus.collect { status ->
                        when (status) {
                            is ProfileEditViewModel.UpdateStatus.Loading -> {
                                dialog!!.setMessage(status.message)
                                dialog!!.show()
                            }
                            is ProfileEditViewModel.UpdateStatus.Success -> {
                                dialog!!.dismiss()
                                Toast.makeText(this@ProfileEditActivity, status.message, Toast.LENGTH_SHORT).show()
                                finish()
                            }
                            is ProfileEditViewModel.UpdateStatus.Error -> {
                                dialog!!.dismiss()
                                Toast.makeText(this@ProfileEditActivity, status.message, Toast.LENGTH_SHORT).show()
                            }
                            ProfileEditViewModel.UpdateStatus.Idle -> {}
                        }
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == RESULT_OK) {
            val uri = CropImage.getPickImageResultUri(this, data)
            if (CropImage.isReadExternalStoragePermissionsRequired(this, uri)) {
                imageUri = uri
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 0)
            } else {
                VOID.CropImageSquare(this)
            }
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                imageUri = result.uri
                binding!!.profileImage.setImageURI(imageUri)
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
                Toast.makeText(this, "Error! $error", Toast.LENGTH_SHORT).show()
            }
        }
    }
}