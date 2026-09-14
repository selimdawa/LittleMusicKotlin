package com.flatcode.littlemusic.viewmodel

import androidx.lifecycle.viewModelScope
import com.flatcode.littlemusic.model.Category
import com.flatcode.littlemusic.model.Song
import com.flatcode.littlemusic.repository.MusicRepository
import com.flatcode.littlemusic.repository.ToolsRepository
import com.flatcode.littlemusic.utils.DATA
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val musicRepository: MusicRepository,
    private val toolsRepository: ToolsRepository
) : BaseViewModel() {

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories

    private val _editorsChoiceSongs = MutableStateFlow<List<Song>>(emptyList())
    val editorsChoiceSongs: StateFlow<List<Song>> = _editorsChoiceSongs

    private val _mostViewedSongs = MutableStateFlow<List<Song>>(emptyList())
    val mostViewedSongs: StateFlow<List<Song>> = _mostViewedSongs

    private val _mostLovedSongs = MutableStateFlow<List<Song>>(emptyList())
    val mostLovedSongs: StateFlow<List<Song>> = _mostLovedSongs

    private val _latestSongs = MutableStateFlow<List<Song>>(emptyList())
    val latestSongs: StateFlow<List<Song>> = _latestSongs

    private val _sliderCount = MutableStateFlow(0)
    val sliderCount: StateFlow<Int> = _sliderCount

    fun loadCategories() {
        viewModelScope.launch {
            musicRepository.getCategories().collect { list ->
                _categories.value = list
            }
        }
    }

    fun loadSliderCount() {
        viewModelScope.launch {
            toolsRepository.getSliderCount().collect { count ->
                _sliderCount.value = count
            }
        }
    }

    fun loadSongs() {
        viewModelScope.launch {
            launch {
                musicRepository.getSongs(DATA.EDITORS_CHOICE).collect { songs ->
                    _editorsChoiceSongs.value = songs.filter { it.editorsChoice in 1..5 }
                }
            }
            launch {
                musicRepository.getSongs(DATA.VIEWS_COUNT, DATA.ORDER_MAIN).collect { songs ->
                    _mostViewedSongs.value = songs
                }
            }
            launch {
                musicRepository.getSongs(DATA.LOVES_COUNT, DATA.ORDER_MAIN).collect { songs ->
                    _mostLovedSongs.value = songs
                }
            }
            launch {
                musicRepository.getSongs(DATA.TIMESTAMP, DATA.ORDER_MAIN).collect { songs ->
                    _latestSongs.value = songs
                }
            }
        }
    }
}