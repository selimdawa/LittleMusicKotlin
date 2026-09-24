package com.flatcode.littlemusic.ui.profile

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.IntentCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.flatcode.littlemusic.R
import com.flatcode.littlemusic.databinding.ActivityProfileEditBinding
import com.flatcode.littlemusic.utils.ProgressDialog
import com.flatcode.littlemusic.utils.loadImage
import com.flatcode.littlemusic.utils.startCropActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class ProfileEditActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileEditBinding
    private val viewModel: ProfileEditViewModel by viewModels()
    private var imageUri: Uri? = null
    private var dialog: ProgressDialog? = null

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            cropActivityLauncher.launch(startCropActivity(uri, isOval = true))
        }
    }

    private val cropActivityLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            val croppedUri = IntentCompat.getParcelableExtra(
                result.data!!, "CROP_RESULT_URI", Uri::class.java
            )
            if (croppedUri != null) {
                imageUri = croppedUri
                binding.profileImage.setImageURI(imageUri)
            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.entries.all { it.value }
        if (granted) {
            launchImagePicker()
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkStoragePermission() {
        val permissions = mutableListOf<String>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }

        val allGranted = permissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }

        if (allGranted) {
            launchImagePicker()
        } else {
            requestPermissionLauncher.launch(permissions.toTypedArray())
        }
    }

    private fun launchImagePicker() {
        pickImageLauncher.launch("image/*")
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
        binding.toolbar.back.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        binding.image.setOnClickListener {
            checkStoragePermission()
        }
        binding.go.setOnClickListener {
            viewModel.updateProfile(binding.nameEt.text.toString().trim(), imageUri)
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
                        binding.profileImage.loadImage(profileImage, true)
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
                                Toast.makeText(
                                    this@ProfileEditActivity, status.message, Toast.LENGTH_SHORT
                                ).show()
                                finish()
                            }

                            is ProfileEditViewModel.UpdateStatus.Error -> {
                                dialog!!.dismiss()
                                Toast.makeText(
                                    this@ProfileEditActivity, status.message, Toast.LENGTH_SHORT
                                ).show()
                            }

                            ProfileEditViewModel.UpdateStatus.Idle -> {}
                        }
                    }
                }
            }
        }
    }
}
