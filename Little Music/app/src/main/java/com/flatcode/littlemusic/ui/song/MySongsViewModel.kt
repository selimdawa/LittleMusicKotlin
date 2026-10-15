package com.flatcode.littlemusic.ui.song

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
class MySongsViewModel @Inject constructor(
    private val musicRepository: MusicRepository
) : BaseViewModel() {

    private val _songs = MutableStateFlow<List<Song>>(emptyList())
    val songs: StateFlow<List<Song>> = _songs

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _currentType = MutableStateFlow(DATA.TIMESTAMP)
    private val _currentDB = MutableStateFlow(DATA.ARTISTS)

    fun setType(type: String) {
        _currentType.value = type
        if (_songs.value.isNotEmpty()) {
            _songs.value = sortSongs(_songs.value, type)
        } else {
            getData()
        }
    }

    fun setDB(db: String) {
        _currentDB.value = db
        getData()
    }

    fun getData() {
        viewModelScope.launch {
            if (_songs.value.isEmpty()) {
                _isLoading.value = true
            }
            val typeDB = _currentDB.value
            val orderBy = _currentType.value

            musicRepository.getInterestedIds(DATA.FirebaseUserUid, typeDB)
                .collectLatest { interestedIds ->
                    musicRepository.getSongs(orderBy).collectLatest { allSongs ->
                        val filtered = allSongs.filter { song ->
                            when (typeDB) {
                                DATA.ARTISTS -> song.artistId in interestedIds
                                DATA.ALBUMS -> song.albumId in interestedIds
                                DATA.CATEGORIES -> song.categoryId in interestedIds
                                else -> false
                            }
                        }
                        _songs.value = sortSongs(filtered, _currentType.value)
                        _isLoading.value = false
                    }
                }
        }
    }

    private fun sortSongs(list: List<Song>, type: String): List<Song> {
        return when (type) {
            DATA.VIEWS_COUNT -> list.sortedByDescending { it.viewsCount }
            DATA.LOVES_COUNT -> list.sortedByDescending { it.lovesCount }
            DATA.NAME -> list.sortedBy { it.name?.lowercase() ?: "" }
            else -> list.sortedByDescending { it.timestamp }
        }
    }
}
