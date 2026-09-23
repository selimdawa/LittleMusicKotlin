package com.flatcode.littlemusicadmin.ui.song

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flatcode.littlemusicadmin.model.Album
import com.flatcode.littlemusicadmin.model.Song
import com.flatcode.littlemusicadmin.repository.AlbumRepository
import com.flatcode.littlemusicadmin.repository.SongRepository
import com.flatcode.littlemusicadmin.utils.DATA
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
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
class CategorySongsViewModel @Inject constructor(
    private val albumRepository: AlbumRepository, private val songRepository: SongRepository
) : ViewModel() {

    private val _allAlbums = MutableStateFlow<List<Album>>(emptyList())
    private val _allSongs = MutableStateFlow<List<Song>>(emptyList())

    private val _searchQuery = MutableStateFlow("")

    val albums: Flow<List<Album>> = combine(_allAlbums, _searchQuery) { list, query ->
        if (query.isEmpty()) list
        else list.filter { it.name?.contains(query, ignoreCase = true) == true }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val songs: Flow<List<Song>> = combine(_allSongs, _searchQuery) { list, query ->
        if (query.isEmpty()) list
        else list.filter { it.name?.contains(query, ignoreCase = true) == true }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _orderBy = MutableStateFlow(DATA.TIMESTAMP)
    val orderBy: StateFlow<String> = _orderBy.asStateFlow()

    private var currentCategoryId: String? = null

    fun init(categoryId: String?) {
        if (currentCategoryId == categoryId) return
        currentCategoryId = categoryId
        fetchData()
    }

    private fun fetchData() {
        viewModelScope.launch {
            _orderBy.collectLatest { order ->
                currentCategoryId?.let { id ->
                    _isLoading.value = true
                    launch {
                        albumRepository.getAlbumsByCategory(id, order).collectLatest {
                            _allAlbums.value = it
                            _isLoading.value = false
                        }
                    }
                    launch {
                        songRepository.getSongsByCategory(id, order).collectLatest {
                            _allSongs.value = it
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