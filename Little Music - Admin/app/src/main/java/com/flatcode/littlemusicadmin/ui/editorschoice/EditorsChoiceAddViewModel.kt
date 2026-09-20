package com.flatcode.littlemusicadmin.ui.editorschoice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flatcode.littlemusicadmin.model.Song
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
class EditorsChoiceAddViewModel @Inject constructor(
    private val repository: SongRepository
) : ViewModel() {

    private val _songs = MutableStateFlow<List<Song>>(emptyList())
    private val _searchQuery = MutableStateFlow("")

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
    private val _isFavorites = MutableStateFlow(false)

    init {
        fetchSongs()
    }

    private fun fetchSongs() {
        viewModelScope.launch {
            combine(_orderBy, _isFavorites) { order, isFav ->
                Pair(order, isFav)
            }.collectLatest { (order, isFav) ->
                _isLoading.value = true
                val flow = if (isFav) {
                    repository.getFavoritesNotInEditorsChoice(order)
                } else {
                    repository.getSongsNotInEditorsChoice(order)
                }
                flow.collectLatest {
                    _songs.value = it
                    _isLoading.value = false
                }
            }
        }
    }

    fun setOrderBy(order: String) {
        _isFavorites.value = false
        _orderBy.value = order
    }

    fun setFavoritesOnly(isFav: Boolean) {
        _isFavorites.value = isFav
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }
}
