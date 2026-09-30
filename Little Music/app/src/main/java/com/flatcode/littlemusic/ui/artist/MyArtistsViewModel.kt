package com.flatcode.littlemusic.ui.artist

import androidx.lifecycle.viewModelScope
import com.flatcode.littlemusic.model.Artist
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
class MyArtistsViewModel @Inject constructor(
    private val musicRepository: MusicRepository
) : BaseViewModel() {

    private val _artists = MutableStateFlow<List<Artist>>(emptyList())
    val artists: StateFlow<List<Artist>> = _artists

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
            musicRepository.getInterestedArtists(DATA.FirebaseUserUid).collectLatest { list ->
                val sorted = when (_currentType.value) {
                    DATA.SONGS_COUNT -> list.sortedByDescending { it.songsCount }
                    DATA.ALBUMS_COUNT -> list.sortedByDescending { it.albumsCount }
                    DATA.INTERESTED_COUNT -> list.sortedByDescending { it.interestedCount }
                    DATA.NAME -> list.sortedBy { it.name?.lowercase() ?: "" }
                    else -> list.sortedByDescending { it.timestamp }
                }
                _artists.value = sorted
                _isLoading.value = false
            }
        }
    }
}