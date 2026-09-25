package com.flatcode.littlemusicadmin.ui.album

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import com.flatcode.littlemusicadmin.utils.BaseActivity
import androidx.lifecycle.lifecycleScope
import coil3.load
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.flatcode.littlemusicadmin.R
import com.flatcode.littlemusicadmin.databinding.ActivityAlbumAddBinding
import com.flatcode.littlemusicadmin.utils.DATA
import com.flatcode.littlemusicadmin.utils.getFileExtension
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
    private var dialog: AlertDialog? = null

    private var selectedCategoryId: String? = null
    private var selectedCategoryTitle: String? = null
    private var selectedArtistId: String? = null
    private var selectedArtistTitle: String? = null

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
        activity = this@AlbumEditActivity
        binding = ActivityAlbumAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        albumId = intent.getStringExtra(DATA.ALBUM_ID)

        dialog = AlertDialog.Builder(context).apply {
            setTitle("Please wait...")
            setCancelable(false)
        }.create()

        initUI()
        observeViewModel()

        albumId?.let { viewModel.loadAlbum(it) }
    }

    private fun initUI() {
        binding.toolbar.nameSpace.setText(R.string.edit_album)
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
                    dialog!!.setMessage("Updating Album...")
                    dialog!!.show()
                } else {
                    dialog!!.dismiss()
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
        AlertDialog.Builder(context)
            .setTitle("Pick Category")
            .setItems(items) { _, which ->
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
        AlertDialog.Builder(context)
            .setTitle("Pick Artist")
            .setItems(items) { _, which ->
                selectedArtistTitle = artists[which].name
                selectedArtistId = artists[which].id
                binding.artist.text = selectedArtistTitle
            }.show()
    }
}