package com.flatcode.littlemusicadmin.Activity

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import coil3.load
import com.flatcode.littlemusicadmin.R
import com.flatcode.littlemusicadmin.Unit.DATA
import com.flatcode.littlemusicadmin.Unit.VOID
import com.flatcode.littlemusicadmin.ViewModel.AlbumEditViewModel
import com.flatcode.littlemusicadmin.databinding.ActivityAlbumAddBinding
import com.theartofdev.edmodo.cropper.CropImage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class AlbumEditActivity : AppCompatActivity() {

    private var binding: ActivityAlbumAddBinding? = null
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = this@AlbumEditActivity
        binding = ActivityAlbumAddBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        albumId = intent.getStringExtra(DATA.ALBUM_ID)

        dialog = ProgressDialog(context)
        dialog!!.setTitle("Please wait...")
        dialog!!.setCanceledOnTouchOutside(false)

        initUI()
        observeViewModel()

        albumId?.let { viewModel.loadAlbum(it) }
    }

    private fun initUI() {
        binding!!.toolbar.nameSpace.setText(R.string.edit_album)
        binding!!.toolbar.back.setOnClickListener { onBackPressed() }
        binding!!.image.setOnClickListener { VOID.CropImageSquare(activity) }
        binding!!.category.setOnClickListener { categoryPickDialog() }
        binding!!.artist.setOnClickListener { artistPickDialog() }
        binding!!.toolbar.ok.setOnClickListener { validateData() }
    }

    private fun validateData() {
        val name = binding!!.nameEt.text.toString().trim()

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
                imageUri?.let { VOID.getFileExtension(it, context) },
                viewModel.album.value?.categoryId,
                viewModel.album.value?.artistId
            )
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.album.collectLatest { album ->
                album?.let {
                    binding!!.nameEt.setText(it.name)
                    binding!!.image.load(it.image)
                    selectedCategoryId = it.categoryId
                    selectedArtistId = it.artistId
                }
            }
        }

        lifecycleScope.launch {
            viewModel.categoryName.collectLatest { name ->
                name?.let {
                    selectedCategoryTitle = it
                    binding!!.category.text = it
                }
            }
        }

        lifecycleScope.launch {
            viewModel.artistName.collectLatest { name ->
                name?.let {
                    selectedArtistTitle = it
                    binding!!.artist.text = it
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
                binding!!.category.text = selectedCategoryTitle
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
                binding!!.artist.text = selectedArtistTitle
            }.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == RESULT_OK) {
            val uri = CropImage.getPickImageResultUri(context, data)
            if (CropImage.isReadExternalStoragePermissionsRequired(context, uri)) {
                imageUri = uri
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 0)
            } else {
                VOID.CropImageSquare(activity)
            }
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                imageUri = result.uri
                binding!!.image.setImageURI(imageUri)
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
                Toast.makeText(this, "Error! $error", Toast.LENGTH_SHORT).show()
            }
        }
    }
}