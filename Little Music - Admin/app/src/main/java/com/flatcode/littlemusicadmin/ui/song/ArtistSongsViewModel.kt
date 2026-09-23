package com.flatcode.littlemusicadmin.ui.song

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flatcode.littlemusicadmin.model.Album
import com.flatcode.littlemusicadmin.model.Song
import com.flatcode.littlemusicadmin.repository.AlbumRepository
import com.flatcode.littlemusicadmin.repository.SongRepository
import com.flatcode.littlemusicadmin.utils.DATA
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArtistSongsViewModel @Inject constructor(
    private val albumRepository: AlbumRepository, private val songRepository: SongRepository
) : ViewModel() {

    private val _albums = MutableStateFlow<List<Album>>(emptyList())
    private val _songs = MutableStateFlow<List<Song>>(emptyList())
    private val _searchQuery = MutableStateFlow("")

    val albums: StateFlow<List<Album>> = combine(_albums, _searchQuery) { albums, query ->
        if (query.isEmpty()) {
            albums
        } else {
            albums.filter {
                it.name?.contains(query, ignoreCase = true) == true
            }
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val songs: StateFlow<List<Song>> = combine(_songs, _searchQuery) { songs, query ->
        if (query.isEmpty()) {
            songs
        } else {
            songs.filter {
                it.name?.contains(query, ignoreCase = true) == true
            }
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

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

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }
}
