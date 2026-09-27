package com.flatcode.littlemusic.ui.settings

import androidx.lifecycle.viewModelScope
import com.flatcode.littlemusic.model.User
import com.flatcode.littlemusic.repository.UserRepository
import com.flatcode.littlemusic.ui.BaseViewModel
import com.flatcode.littlemusic.utils.DATA
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userRepository: UserRepository
) : BaseViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    private val _itemCounts = MutableStateFlow<Map<String, Int>>(emptyMap())
    val itemCounts: StateFlow<Map<String, Int>> = _itemCounts.asStateFlow()

    fun loadUserInfo() {
        viewModelScope.launch {
            userRepository.getUserInfo(DATA.FirebaseUserUid).collect { user ->
                _user.value = user
            }
        }
    }

    fun loadItemCounts() {
        val uid = DATA.FirebaseUserUid
        val counts = mutableMapOf<String, Int>()

        viewModelScope.launch {
            userRepository.getInterestedCount(uid, DATA.ALBUMS).collect { alb ->
                counts[DATA.ALBUMS] = alb.toInt()
                _itemCounts.value = counts.toMap()
            }
        }
        viewModelScope.launch {
            userRepository.getInterestedCount(uid, DATA.ARTISTS).collect { art ->
                counts[DATA.ARTISTS] = art.toInt()
                _itemCounts.value = counts.toMap()
            }
        }
        viewModelScope.launch {
            userRepository.getInterestedCount(uid, DATA.CATEGORIES).collect { cat ->
                counts[DATA.CATEGORIES] = cat.toInt()
                _itemCounts.value = counts.toMap()
            }
        }
        viewModelScope.launch {
            userRepository.getCount(uid, DATA.FAVORITES).collect { fav ->
                counts[DATA.FAVORITES] = fav.toInt()
                _itemCounts.value = counts.toMap()
            }
        }
    }
}
