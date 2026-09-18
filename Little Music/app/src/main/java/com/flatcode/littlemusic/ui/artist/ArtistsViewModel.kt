package com.flatcode.littlemusic.ui.artist

import androidx.lifecycle.viewModelScope
import com.flatcode.littlemusic.model.Artist
import com.flatcode.littlemusic.repository.MusicRepository
import com.flatcode.littlemusic.utils.DATA
import com.flatcode.littlemusic.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArtistsViewModel @Inject constructor(
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
            musicRepository.getArtists(_currentType.value).collect { list ->
                _artists.value = list
                _isLoading.value = false
            }
        }
    }
}