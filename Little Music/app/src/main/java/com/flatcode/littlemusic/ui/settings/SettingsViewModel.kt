package com.flatcode.littlemusic.ui.settings

import androidx.lifecycle.viewModelScope
import com.flatcode.littlemusic.model.User
import com.flatcode.littlemusic.repository.MusicRepository
import com.flatcode.littlemusic.repository.UserRepository
import com.flatcode.littlemusic.ui.BaseViewModel
import com.flatcode.littlemusic.utils.DATA
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val musicRepository: MusicRepository
) : BaseViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    private val _itemCounts = MutableStateFlow<Map<String, Int>>(emptyMap())
    val itemCounts: StateFlow<Map<String, Int>> = _itemCounts.asStateFlow()

    fun loadUserInfo() {
        viewModelScope.launch {
            userRepository.getUserInfo(DATA.FirebaseUserUid).collectLatest { user ->
                _user.value = user
            }
        }
    }

    fun loadItemCounts() {
        val uid = DATA.FirebaseUserUid
        val counts = mutableMapOf<String, Int>()

        viewModelScope.launch {
            musicRepository.getInterestedCount(uid, DATA.ALBUMS).collectLatest { alb ->
                counts[DATA.ALBUMS] = alb
                _itemCounts.value = counts.toMap()
            }
        }
        viewModelScope.launch {
            musicRepository.getInterestedCount(uid, DATA.ARTISTS).collectLatest { art ->
                counts[DATA.ARTISTS] = art
                _itemCounts.value = counts.toMap()
            }
        }
        viewModelScope.launch {
            musicRepository.getInterestedCount(uid, DATA.CATEGORIES).collectLatest { cat ->
                counts[DATA.CATEGORIES] = cat
                _itemCounts.value = counts.toMap()
            }
        }
        viewModelScope.launch {
            musicRepository.getFavoriteCount(uid).collectLatest { fav ->
                counts[DATA.FAVORITES] = fav
                _itemCounts.value = counts.toMap()
            }
        }
    }
}
