package com.flatcode.littlemusic.ui.profile

import android.Manifest
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.flatcode.littlemusic.R
import com.flatcode.littlemusic.utils.DATA
import com.flatcode.littlemusic.utils.glideImage
import com.flatcode.littlemusic.databinding.ActivityProfileEditBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView

@AndroidEntryPoint
class ProfileEditActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileEditBinding
    private val viewModel: ProfileEditViewModel by viewModels()
    private var imageUri: Uri? = null
    private var dialog: ProgressDialog? = null

    private val cropImage = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            imageUri = result.uriContent
            binding.profileImage.setImageURI(imageUri)
        } else {
            val exception = result.error
            if (exception != null) {
                Toast.makeText(this, "Error! ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityProfileEditBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Timber.i("ProfileEditActivity Created")

        ViewCompat.setOnApplyWindowInsetsListener(binding.toolbar.root) { v, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = insets.top + 10
            }
            windowInsets
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.go.parent as View) { v, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(bottom = insets.bottom)
            windowInsets
        }

        dialog = ProgressDialog(this)
        dialog!!.setTitle("Please wait...")
        dialog!!.setCanceledOnTouchOutside(false)

        binding.toolbar.nameSpace.setText(R.string.edit_profile)
        binding.toolbar.back.setOnClickListener { onBackPressed() }
        binding.image.setOnClickListener {
            cropImage.launch(
                CropImageContractOptions(
                    uri = null,
                    cropImageOptions = CropImageOptions(
                        guidelines = CropImageView.Guidelines.ON,
                        aspectRatioX = 1,
                        aspectRatioY = 1,
                        fixAspectRatio = true,
                        cropShape = CropImageView.CropShape.OVAL,
                        minCropResultWidth = DATA.MIX_SQUARE,
                        minCropResultHeight = DATA.MIX_SQUARE
                    )
                )
            )
        }
        binding.go.setOnClickListener {
            viewModel.updateProfile(binding.nameEt.text.toString().trim(), imageUri, this)
        }

        observeViewModel()
        viewModel.loadUserInfo()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.username.collect { username ->
                        binding.nameEt.setText(username)
                    }
                }
                launch {
                    viewModel.profileImage.collect { profileImage ->
                        binding.profileImage.glideImage(profileImage, true)
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
}
