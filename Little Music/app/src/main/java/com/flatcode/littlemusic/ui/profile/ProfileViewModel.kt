package com.flatcode.littlemusic.ui.profile

import androidx.lifecycle.viewModelScope
import com.flatcode.littlemusic.repository.UserRepository
import com.flatcode.littlemusic.ui.BaseViewModel
import com.flatcode.littlemusic.utils.DATA
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : BaseViewModel() {

    private val _username = MutableStateFlow("")
    val username: StateFlow<String> = _username

    private val _profileImage = MutableStateFlow("")
    val profileImage: StateFlow<String> = _profileImage

    private val _favoritesCount = MutableStateFlow(0L)
    val favoritesCount: StateFlow<Long> = _favoritesCount

    private val _albumsCount = MutableStateFlow(0L)
    val albumsCount: StateFlow<Long> = _albumsCount

    private val _artistsCount = MutableStateFlow(0L)
    val artistsCount: StateFlow<Long> = _artistsCount

    private val _categoriesCount = MutableStateFlow(0L)
    val categoriesCount: StateFlow<Long> = _categoriesCount

    fun loadUserInfo(profileId: String) {
        viewModelScope.launch {
            userRepository.getUserInfo(profileId).collect { user ->
                user?.let {
                    _username.value = it.username ?: ""
                    _profileImage.value = it.profileImage ?: ""
                }
            }
        }
    }

    fun loadCounts(profileId: String) {
        viewModelScope.launch {
            launch {
                userRepository.getCount(profileId, DATA.FAVORITES)
                    .collect { _favoritesCount.value = it }
            }
            launch {
                userRepository.getInterestedCount(profileId, DATA.ALBUMS)
                    .collect { _albumsCount.value = it }
            }
            launch {
                userRepository.getInterestedCount(profileId, DATA.ARTISTS)
                    .collect { _artistsCount.value = it }
            }
            launch {
                userRepository.getInterestedCount(profileId, DATA.CATEGORIES)
                    .collect { _categoriesCount.value = it }
            }
        }
    }
}