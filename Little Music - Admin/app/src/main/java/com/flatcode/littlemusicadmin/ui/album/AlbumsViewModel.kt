package com.flatcode.littlemusicadmin.ui.album

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flatcode.littlemusicadmin.model.Album
import com.flatcode.littlemusicadmin.repository.AlbumRepository
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
class AlbumsViewModel @Inject constructor(
    private val repository: AlbumRepository
) : ViewModel() {

    private val _albums = MutableStateFlow<List<Album>>(emptyList())
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

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _orderBy = MutableStateFlow(DATA.TIMESTAMP)
    val orderBy: StateFlow<String> = _orderBy.asStateFlow()

    init {
        fetchAlbums()
    }

    private fun fetchAlbums() {
        viewModelScope.launch {
            _orderBy.collectLatest { order ->
                _isLoading.value = true
                repository.getAlbums(order).collectLatest {
                    _albums.value = it
                    _isLoading.value = false
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
