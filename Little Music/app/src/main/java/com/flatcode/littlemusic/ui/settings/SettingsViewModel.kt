package com.flatcode.littlemusic.ui.settings

import androidx.lifecycle.viewModelScope
import com.flatcode.littlemusic.R
import com.flatcode.littlemusic.model.Setting
import com.flatcode.littlemusic.model.User
import com.flatcode.littlemusic.repository.UserRepository
import com.flatcode.littlemusic.utils.CLASS
import com.flatcode.littlemusic.utils.DATA
import com.flatcode.littlemusic.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userRepository: UserRepository
) : BaseViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    private val _settings = MutableStateFlow<List<Setting>>(emptyList())
    val settings: StateFlow<List<Setting>> = _settings

    fun loadUserInfo() {
        viewModelScope.launch {
            userRepository.getUserInfo(DATA.FirebaseUserUid).collect { user ->
                _user.value = user
            }
        }
    }

    fun loadSettings() {
        viewModelScope.launch {
            val uid = DATA.FirebaseUserUid
            launch {
                userRepository.getInterestedCount(uid, DATA.ALBUMS).collect { alb ->
                    userRepository.getInterestedCount(uid, DATA.ARTISTS).collect { art ->
                        userRepository.getInterestedCount(uid, DATA.CATEGORIES).collect { cat ->
                            userRepository.getCount(uid, DATA.FAVORITES).collect { fav ->
                                buildSettingsList(alb.toInt(), art.toInt(), cat.toInt(), fav.toInt())
                            }
                        }
                    }
                }
            }
        }
    }

    private fun buildSettingsList(alb: Int, art: Int, cat: Int, fav: Int) {
        val list = mutableListOf<Setting>()
        list.add(Setting("1", "Edit Profile", R.drawable.ic_edit_white, 0, CLASS.PROFILE_EDIT))
        list.add(Setting("2", "My Albums", R.drawable.ic_album, alb, CLASS.MY_ALBUMS))
        list.add(Setting("3", "My Artists", R.drawable.ic_mic, art, CLASS.MY_ARTISTS))
        list.add(Setting("4", "My Categories", R.drawable.ic_category_gray, cat, CLASS.MY_CATEGORIES))
        list.add(Setting("5", "Favorites", R.drawable.ic_star_selected, fav, CLASS.FAVORITES))
        list.add(Setting("6", "About App", R.drawable.ic_info, 0, null))
        list.add(Setting("7", "Logout", R.drawable.ic_logout_white, 0, null))
        list.add(Setting("8", "Share App", R.drawable.ic_share, 0, null))
        list.add(Setting("9", "Rate APP", R.drawable.ic_heart_selected, 0, null))
        list.add(Setting("10", "Privacy Policy", R.drawable.ic_privacy_policy, 0, CLASS.PRIVACY_POLICY))
        _settings.value = list
    }
}