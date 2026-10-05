package com.flatcode.littlemusicadmin.ui.album

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
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.IntentCompat
import androidx.lifecycle.lifecycleScope
import coil3.load
import com.flatcode.littlemusicadmin.R
import com.flatcode.littlemusicadmin.databinding.ActivityAlbumAddBinding
import com.flatcode.littlemusicadmin.utils.BaseActivity
import com.flatcode.littlemusicadmin.utils.DATA
import com.flatcode.littlemusicadmin.utils.ProgressDialog
import com.flatcode.littlemusicadmin.utils.getFileExtension
import com.flatcode.littlemusicadmin.utils.isNetworkAvailable
import com.flatcode.littlemusicadmin.utils.startCropActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class AlbumEditActivity : BaseActivity() {

    private lateinit var binding: ActivityAlbumAddBinding
    private val viewModel: AlbumEditViewModel by viewModels()

    private var activity: Activity? = null
    private var context: Context = this@AlbumEditActivity
    private var albumId: String? = null
    private var imageUri: Uri? = null
    private var dialog: ProgressDialog? = null

    private var selectedCategoryId: String? = null
    private var selectedCategoryTitle: String? = null
    private var selectedArtistId: String? = null
    private var selectedArtistTitle: String? = null

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
                cropImageLauncher.launch(context.startCropActivity(it, 1, 1, false))
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
        activity = this@AlbumEditActivity
        binding = ActivityAlbumAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        albumId = intent.getStringExtra(DATA.ALBUM_ID)

        dialog = ProgressDialog(this).apply {
            setTitle("Please wait...")
            setCanceledOnTouchOutside(false)
        }

        initUI()
        observeViewModel()

        albumId?.let { viewModel.loadAlbum(it) }
    }

    private fun initUI() {
        binding.toolbar.nameSpace.setText(R.string.edit_album)
        binding.toolbar.back.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        binding.image.setOnClickListener {
            checkPermissionAndPickImage()
        }
        binding.category.setOnClickListener { categoryPickDialog() }
        binding.artist.setOnClickListener { artistPickDialog() }
        binding.toolbar.ok.setOnClickListener { validateData() }
    }

    private fun validateData() {
        val name = binding.nameEt.text.toString().trim()

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(context, "Enter Name...", Toast.LENGTH_SHORT).show()
        } else if (TextUtils.isEmpty(selectedArtistId)) {
            Toast.makeText(context, "Enter Artist...", Toast.LENGTH_SHORT).show()
        } else if (TextUtils.isEmpty(selectedCategoryId)) {
            Toast.makeText(context, "Enter Category...", Toast.LENGTH_SHORT).show()
        } else if (!isNetworkAvailable()) {
            Toast.makeText(context, getString(R.string.no_internet_connection), Toast.LENGTH_SHORT)
                .show()
        } else {
            viewModel.updateAlbum(
                albumId!!,
                name,
                selectedCategoryId!!,
                selectedArtistId!!,
                imageUri,
                imageUri?.getFileExtension(context),
                viewModel.album.value?.categoryId,
                viewModel.album.value?.artistId
            )
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.album.collectLatest { album ->
                album?.let {
                    binding.nameEt.setText(it.name)
                    binding.image.load(it.image)
                    selectedCategoryId = it.categoryId
                    selectedArtistId = it.artistId
                }
            }
        }

        lifecycleScope.launch {
            viewModel.categoryName.collectLatest { name ->
                name?.let {
                    selectedCategoryTitle = it
                    binding.category.text = it
                }
            }
        }

        lifecycleScope.launch {
            viewModel.artistName.collectLatest { name ->
                name?.let {
                    selectedArtistTitle = it
                    binding.artist.text = it
                }
            }
        }

        lifecycleScope.launch {
            viewModel.isLoading.collectLatest { isLoading ->
                if (isLoading) {
                    dialog?.setMessage("Updating Album...")
                    dialog?.show()
                } else {
                    dialog?.dismiss()
                }
            }
        }

        lifecycleScope.launch {
            viewModel.updateAlbumSuccess.collectLatest { success ->
                if (success == true) {
                    Toast.makeText(context, "Successfully updated...", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }

        lifecycleScope.launch {
            viewModel.errorMessage.collectLatest { error ->
                if (error != null) {
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                    Timber.e(error)
                }
            }
        }
    }

    private fun categoryPickDialog() {
        val categories = viewModel.categories.value
        if (categories.isEmpty()) {
            Toast.makeText(context, "Categories not loaded yet", Toast.LENGTH_SHORT).show()
            return
        }

        val items = categories.map { it.name }.toTypedArray()
        AlertDialog.Builder(context).setTitle("Pick Category").setItems(items) { _, which ->
                selectedCategoryTitle = categories[which].name
                selectedCategoryId = categories[which].id
                binding.category.text = selectedCategoryTitle
            }.show()
    }

    private fun artistPickDialog() {
        val artists = viewModel.artists.value
        if (artists.isEmpty()) {
            Toast.makeText(context, "Artists not loaded yet", Toast.LENGTH_SHORT).show()
            return
        }

        val items = artists.map { it.name }.toTypedArray()
        AlertDialog.Builder(context).setTitle("Pick Artist").setItems(items) { _, which ->
                selectedArtistTitle = artists[which].name
                selectedArtistId = artists[which].id
                binding.artist.text = selectedArtistTitle
            }.show()
    }
}