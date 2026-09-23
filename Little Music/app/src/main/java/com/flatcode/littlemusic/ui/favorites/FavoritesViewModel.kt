package com.flatcode.littlemusic.ui.favorites

import androidx.lifecycle.viewModelScope
import com.flatcode.littlemusic.model.Song
import com.flatcode.littlemusic.repository.MusicRepository
import com.flatcode.littlemusic.ui.BaseViewModel
import com.flatcode.littlemusic.utils.DATA
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
            _isLoading.value = true
            musicRepository.getFavoriteIds(DATA.FirebaseUserUid).collect { favoriteIds ->
                musicRepository.getSongs(_currentType.value).collect { allSongs ->
                    _songs.value = allSongs.filter { it.id in favoriteIds }
                    _isLoading.value = false
                }
            }
        }
    }
}