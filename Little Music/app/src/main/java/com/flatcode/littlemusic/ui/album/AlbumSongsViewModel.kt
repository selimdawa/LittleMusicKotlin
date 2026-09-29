package com.flatcode.littlemusic.ui.album

import androidx.lifecycle.viewModelScope
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
class AlbumSongsViewModel @Inject constructor(
    private val musicRepository: MusicRepository
) : BaseViewModel() {

    private val _songs = MutableStateFlow<List<Song>>(emptyList())
    val songs: StateFlow<List<Song>> = _songs

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _currentType = MutableStateFlow(DATA.TIMESTAMP)
    val currentType: StateFlow<String> = _currentType

    fun setType(type: String, albumId: String) {
        _currentType.value = type
        getData(albumId)
    }

    fun getData(albumId: String?) {
        if (albumId == null) return
        viewModelScope.launch {
            _isLoading.value = true
            musicRepository.getSongsByAlbum(albumId).collectLatest { list ->
                val sortedList = when (_currentType.value) {
                    DATA.VIEWS_COUNT -> list.sortedByDescending { it.viewsCount }
                    DATA.LOVES_COUNT -> list.sortedByDescending { it.lovesCount }
                    DATA.NAME -> list.sortedBy { it.name }
                    else -> list.sortedByDescending { it.timestamp }
                }
                _songs.value = sortedList
                _isLoading.value = false
            }
        }
    }
}
