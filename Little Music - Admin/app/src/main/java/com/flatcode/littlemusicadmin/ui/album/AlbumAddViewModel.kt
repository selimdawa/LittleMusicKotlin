package com.flatcode.littlemusicadmin.ui.album

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlbumAddViewModel @Inject constructor(
    private val albumRepository: AlbumRepository,
    private val categoryRepository: CategoryRepository,
    private val artistRepository: ArtistRepository,
    private val commonRepository: CommonRepository
) : ViewModel() {

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    private val _artists = MutableStateFlow<List<Artist>>(emptyList())
    val artists: StateFlow<List<Artist>> = _artists.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _addAlbumSuccess = MutableStateFlow<Boolean?>(null)
    val addAlbumSuccess: StateFlow<Boolean?> = _addAlbumSuccess.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        fetchCategories()
        fetchArtists()
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

    fun addAlbum(name: String, categoryId: String, artistId: String, imageUri: Uri, extension: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _addAlbumSuccess.value = null
            _errorMessage.value = null

            val albumId = commonRepository.getNewKey(DATA.ALBUMS)
            if (albumId == null) {
                _errorMessage.value = "Failed to generate album ID"
                _isLoading.value = false
                return@launch
            }

            val path = "Images/Album/$albumId.$extension"

            val imageUrl = commonRepository.uploadImage(imageUri, path)
            if (imageUrl != null) {
                val success = albumRepository.addAlbum(albumId, name, categoryId, artistId, imageUrl)
                if (success) {
                    _addAlbumSuccess.value = true
                } else {
                    _errorMessage.value = "Failed to add album to database"
                }
            } else {
                _errorMessage.value = "Failed to upload image"
            }
            _isLoading.value = false
        }
    }

    fun resetState() {
        _addAlbumSuccess.value = null
        _errorMessage.value = null
    }
}
