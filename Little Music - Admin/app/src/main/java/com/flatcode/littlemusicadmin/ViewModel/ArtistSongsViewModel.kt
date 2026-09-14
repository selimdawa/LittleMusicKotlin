package com.flatcode.littlemusicadmin.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flatcode.littlemusicadmin.Model.Album
import com.flatcode.littlemusicadmin.Model.Song
import com.flatcode.littlemusicadmin.Repository.AlbumRepository
import com.flatcode.littlemusicadmin.Repository.SongRepository
import com.flatcode.littlemusicadmin.Unit.DATA
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArtistSongsViewModel @Inject constructor(
    private val albumRepository: AlbumRepository,
    private val songRepository: SongRepository
) : ViewModel() {

    private val _albums = MutableStateFlow<List<Album>>(emptyList())
    val albums: StateFlow<List<Album>> = _albums.asStateFlow()

    private val _songs = MutableStateFlow<List<Song>>(emptyList())
    val songs: StateFlow<List<Song>> = _songs.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _orderBy = MutableStateFlow(DATA.TIMESTAMP)
    val orderBy: StateFlow<String> = _orderBy.asStateFlow()

    private var currentArtistId: String? = null

    fun init(artistId: String?) {
        if (currentArtistId == artistId) return
        currentArtistId = artistId
        fetchData()
    }

    private fun fetchData() {
        viewModelScope.launch {
            _orderBy.collectLatest { order ->
                currentArtistId?.let { id ->
                    _isLoading.value = true
                    launch {
                        albumRepository.getAlbumsByArtist(id, order).collectLatest {
                            _albums.value = it
                            _isLoading.value = false
                        }
                    }
                    launch {
                        songRepository.getSongsByArtist(id, order).collectLatest {
                            _songs.value = it
                            _isLoading.value = false
                        }
                    }
                }
            }
        }
    }

    fun setOrderBy(order: String) {
        _orderBy.value = order
    }
}
