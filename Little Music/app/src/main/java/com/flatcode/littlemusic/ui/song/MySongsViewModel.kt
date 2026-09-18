package com.flatcode.littlemusic.ui.song

import androidx.lifecycle.viewModelScope
import com.flatcode.littlemusic.model.Song
import com.flatcode.littlemusic.repository.MusicRepository
import com.flatcode.littlemusic.utils.DATA
import com.flatcode.littlemusic.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
        getData()
    }

    fun setDB(db: String) {
        _currentDB.value = db
        getData()
    }

    fun getData() {
        viewModelScope.launch {
            _isLoading.value = true
            val typeDB = _currentDB.value
            val orderBy = _currentType.value
            
            musicRepository.getInterestedIds(DATA.FirebaseUserUid, typeDB).collect { interestedIds ->
                musicRepository.getSongs(orderBy).collect { allSongs ->
                    val filtered = allSongs.filter { song ->
                        when (typeDB) {
                            DATA.ARTISTS -> song.artistId in interestedIds
                            DATA.ALBUMS -> song.albumId in interestedIds
                            DATA.CATEGORIES -> song.categoryId in interestedIds
                            else -> false
                        }
                    }
                    _songs.value = filtered
                    _isLoading.value = false
                }
            }
        }
    }
}