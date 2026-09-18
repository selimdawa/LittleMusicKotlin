package com.flatcode.littlemusic.ui.artist

import androidx.lifecycle.viewModelScope
import com.flatcode.littlemusic.model.Album
import com.flatcode.littlemusic.model.Song
import com.flatcode.littlemusic.repository.MusicRepository
import com.flatcode.littlemusic.utils.DATA
import com.flatcode.littlemusic.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArtistSongsViewModel @Inject constructor(
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

    fun setType(type: String, artistId: String, isAlbum: Boolean) {
        _currentType.value = type
        if (isAlbum) getAlbums(artistId) else getSongs(artistId)
    }

    fun getAlbums(artistId: String?) {
        if (artistId == null) return
        viewModelScope.launch {
            _isLoading.value = true
            musicRepository.getAlbums(_currentType.value).collect { list ->
                _albums.value = list.filter { it.artistId == artistId }
                _isLoading.value = false
            }
        }
    }

    fun getSongs(artistId: String?) {
        if (artistId == null) return
        viewModelScope.launch {
            _isLoading.value = true
            musicRepository.getSongs(_currentType.value).collect { list ->
                _songs.value = list.filter { it.artistId == artistId }
                _isLoading.value = false
            }
        }
    }
}