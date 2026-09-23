package com.flatcode.littlemusic.ui.album

import androidx.lifecycle.viewModelScope
import com.flatcode.littlemusic.model.Album
import com.flatcode.littlemusic.repository.MusicRepository
import com.flatcode.littlemusic.ui.BaseViewModel
import com.flatcode.littlemusic.utils.DATA
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyAlbumsViewModel @Inject constructor(
    private val musicRepository: MusicRepository
) : BaseViewModel() {

    private val _albums = MutableStateFlow<List<Album>>(emptyList())
    val albums: StateFlow<List<Album>> = _albums

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
            musicRepository.getInterestedIds(DATA.FirebaseUserUid, DATA.ALBUMS)
                .collect { interestedIds ->
                    musicRepository.getAlbums(_currentType.value).collect { allAlbums ->
                        _albums.value = allAlbums.filter { it.id in interestedIds }
                        _isLoading.value = false
                    }
                }
        }
    }
}