package com.flatcode.littlemusicadmin.ui.album

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flatcode.littlemusicadmin.model.Album
import com.flatcode.littlemusicadmin.model.Artist
import com.flatcode.littlemusicadmin.model.Category
import com.flatcode.littlemusicadmin.repository.AlbumRepository
import com.flatcode.littlemusicadmin.repository.ArtistRepository
import com.flatcode.littlemusicadmin.repository.CategoryRepository
import com.flatcode.littlemusicadmin.repository.CommonRepository
import com.flatcode.littlemusicadmin.utils.DATA
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlbumEditViewModel @Inject constructor(
    private val albumRepository: AlbumRepository,
    private val categoryRepository: CategoryRepository,
    private val artistRepository: ArtistRepository,
    private val commonRepository: CommonRepository
) : ViewModel() {

    private val _album = MutableStateFlow<Album?>(null)
    val album: StateFlow<Album?> = _album.asStateFlow()

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    private val _artists = MutableStateFlow<List<Artist>>(emptyList())
    val artists: StateFlow<List<Artist>> = _artists.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _updateAlbumSuccess = MutableStateFlow<Boolean?>(null)
    val updateAlbumSuccess: StateFlow<Boolean?> = _updateAlbumSuccess.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _categoryName = MutableStateFlow<String?>(null)
    val categoryName: StateFlow<String?> = _categoryName.asStateFlow()

    private val _artistName = MutableStateFlow<String?>(null)
    val artistName: StateFlow<String?> = _artistName.asStateFlow()

    init {
        fetchCategories()
        fetchArtists()
    }

    fun loadAlbum(id: String) {
        viewModelScope.launch {
            albumRepository.getAlbumById(id).collectLatest {
                _album.value = it
                it?.categoryId?.let { catId -> loadCategoryName(catId) }
                it?.artistId?.let { artId -> loadArtistName(artId) }
            }
        }
    }

    private fun loadCategoryName(id: String) {
        viewModelScope.launch {
            commonRepository.getNameById(DATA.CATEGORIES, id).collectLatest {
                _categoryName.value = it
            }
        }
    }

    private fun loadArtistName(id: String) {
        viewModelScope.launch {
            commonRepository.getNameById(DATA.ARTISTS, id).collectLatest {
                _artistName.value = it
            }
        }
    }

    private fun fetchCategories() {
        viewModelScope.launch {
            categoryRepository.getCategories(DATA.NAME).collect {
                _categories.value = it
            }
        }
    }

    private fun fetchArtists() {
        viewModelScope.launch {
            artistRepository.getArtists(DATA.NAME).collect {
                _artists.value = it
            }
        }
    }

    fun updateAlbum(
        id: String,
        name: String,
        categoryId: String,
        artistId: String,
        imageUri: Uri?,
        extension: String?,
        oldCategoryId: String?,
        oldArtistId: String?
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _updateAlbumSuccess.value = null
            _errorMessage.value = null

            var imageUrl: String? = null
            if (imageUri != null && extension != null) {
                val path = "Images/Album/$id.$extension"
                imageUrl = commonRepository.uploadImage(imageUri, path)
                if (imageUrl == null) {
                    _errorMessage.value = "Failed to upload image"
                    _isLoading.value = false
                    return@launch
                }
            }

            val success = albumRepository.updateAlbum(
                id, name, categoryId, artistId, imageUrl, oldCategoryId, oldArtistId
            )
            if (success) {
                _updateAlbumSuccess.value = true
            } else {
                _errorMessage.value = "Failed to update album"
            }
            _isLoading.value = false
        }
    }
}
