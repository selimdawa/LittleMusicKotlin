package com.flatcode.littlemusic.ui.main

import androidx.lifecycle.viewModelScope
import com.flatcode.littlemusic.repository.UserRepository
import com.flatcode.littlemusic.utils.DATA
import com.flatcode.littlemusic.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userRepository: UserRepository
) : BaseViewModel() {

    private val _profileImage = MutableStateFlow<String?>(null)
    val profileImage: StateFlow<String?> = _profileImage

    fun loadUserInfo() {
        viewModelScope.launch {
            userRepository.getUserInfo(DATA.FirebaseUserUid).collect { user ->
                _profileImage.value = user?.profileImage
            }
        }
    }
}