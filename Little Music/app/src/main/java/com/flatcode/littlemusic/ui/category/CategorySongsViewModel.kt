package com.flatcode.littlemusic.ui.category

import androidx.lifecycle.viewModelScope
import com.flatcode.littlemusic.model.Album
import com.flatcode.littlemusic.model.Song
import com.flatcode.littlemusic.repository.MusicRepository
import com.flatcode.littlemusic.ui.BaseViewModel
import com.flatcode.littlemusic.utils.DATA
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategorySongsViewModel @Inject constructor(
    private val musicRepository: MusicRepository
) : BaseViewModel() {

    private val _albums = MutableStateFlow<List<Album>>(emptyList())
    val albums: StateFlow<List<Album>> = _albums

    private val _songs = MutableStateFlow<List<Song>>(emptyList())
    val songs: StateFlow<List<Song>> = _songs

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _currentType = MutableStateFlow(DATA.TIMESTAMP)
    val currentType: StateFlow<String> = _currentType

    fun setType(type: String, categoryId: String, isAlbum: Boolean) {
        _currentType.value = type
        if (isAlbum) {
            if (_albums.value.isNotEmpty()) {
                _albums.value = sortAlbums(_albums.value, type)
            } else {
                getAlbums(categoryId)
            }
        } else {
            if (_songs.value.isNotEmpty()) {
                _songs.value = sortSongs(_songs.value, type)
            } else {
                getSongs(categoryId)
            }
        }
    }

    fun getAlbums(categoryId: String?) {
        if (categoryId == null) return
        if (_albums.value.isEmpty()) {
            _isLoading.value = true
        }
        viewModelScope.launch {
            musicRepository.getAlbums(_currentType.value).collectLatest { list ->
                val categoryAlbums = list.filter { it.categoryId == categoryId }
                _albums.value = sortAlbums(categoryAlbums, _currentType.value)
                _isLoading.value = false
            }
        }
    }

    fun getSongs(categoryId: String?) {
        if (categoryId == null) return
        if (_songs.value.isEmpty()) {
            _isLoading.value = true
        }
        viewModelScope.launch {
            musicRepository.getSongsByCategory(categoryId).collectLatest { list ->
                _songs.value = sortSongs(list, _currentType.value)
                _isLoading.value = false
            }
        }
    }

    private fun sortAlbums(list: List<Album>, type: String): List<Album> {
        return when (type) {
            DATA.SONGS_COUNT -> list.sortedByDescending { it.songsCount }
            DATA.INTERESTED_COUNT -> list.sortedByDescending { it.interestedCount }
            DATA.NAME -> list.sortedBy { it.name?.lowercase() ?: "" }
            else -> list.sortedByDescending { it.timestamp }
        }
    }

    private fun sortSongs(list: List<Song>, type: String): List<Song> {
        return when (type) {
            DATA.VIEWS_COUNT -> list.sortedByDescending { it.viewsCount }
            DATA.LOVES_COUNT -> list.sortedByDescending { it.lovesCount }
            DATA.NAME -> list.sortedBy { it.name?.lowercase() ?: "" }
            else -> list.sortedByDescending { it.timestamp }
        }
    }
}
