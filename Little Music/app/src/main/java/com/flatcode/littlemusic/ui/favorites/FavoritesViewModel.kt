package com.flatcode.littlemusic.ui.favorites

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
class FavoritesViewModel @Inject constructor(
    private val musicRepository: MusicRepository
) : BaseViewModel() {

    private val _songs = MutableStateFlow<List<Song>>(emptyList())
    val songs: StateFlow<List<Song>> = _songs

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _currentType = MutableStateFlow(DATA.TIMESTAMP)
    val currentType: StateFlow<String> = _currentType

    fun setType(type: String) {
        _currentType.value = type
        getData()
    }

    fun getData() {
        viewModelScope.launch {
            musicRepository.getFavoriteSongs(DATA.FirebaseUserUid).collectLatest { favoriteSongs ->
                val sortedSongs = when (_currentType.value) {
                    DATA.VIEWS_COUNT -> favoriteSongs.sortedByDescending { it.viewsCount }
                    DATA.LOVES_COUNT -> favoriteSongs.sortedByDescending { it.lovesCount }
                    DATA.NAME -> favoriteSongs.sortedBy { it.name }
                    else -> favoriteSongs.sortedByDescending { it.timestamp }
                }
                _songs.value = sortedSongs
                _isLoading.value = false
            }
        }
    }
}
