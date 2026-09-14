package com.flatcode.littlemusic.viewmodel

import androidx.lifecycle.viewModelScope
import com.flatcode.littlemusic.model.Song
import com.flatcode.littlemusic.repository.MusicRepository
import com.flatcode.littlemusic.utils.DATA
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShowMoreViewModel @Inject constructor(
    private val musicRepository: MusicRepository
) : BaseViewModel() {

    private val _songs = MutableStateFlow<List<Song>>(emptyList())
    val songs: StateFlow<List<Song>> = _songs

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun getData(orderBy: String?) {
        if (orderBy == null) return
        viewModelScope.launch {
            _isLoading.value = true
            musicRepository.getSongs(orderBy).collect { list ->
                if (orderBy == DATA.EDITORS_CHOICE) {
                    _songs.value = list.filter { it.editorsChoice > 0 }
                } else {
                    _songs.value = list
                }
                _isLoading.value = false
            }
        }
    }
}