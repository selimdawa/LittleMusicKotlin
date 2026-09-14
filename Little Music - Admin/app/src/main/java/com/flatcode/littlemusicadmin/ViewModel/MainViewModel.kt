package com.flatcode.littlemusicadmin.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flatcode.littlemusicadmin.Model.User
import com.flatcode.littlemusicadmin.Repository.ArtistRepository
import com.flatcode.littlemusicadmin.Repository.CategoryRepository
import com.flatcode.littlemusicadmin.Repository.CommonRepository
import com.flatcode.littlemusicadmin.Repository.SongRepository
import com.flatcode.littlemusicadmin.Repository.UserRepository
import com.flatcode.littlemusicadmin.Unit.DATA
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val songRepository: SongRepository,
    private val categoryRepository: CategoryRepository,
    private val commonRepository: CommonRepository
) : ViewModel() {

    private val _userInfo = MutableStateFlow<User?>(null)
    val userInfo: StateFlow<User?> = _userInfo.asStateFlow()

    private val _counts = MutableStateFlow<MainCounts>(MainCounts())
    val counts: StateFlow<MainCounts> = _counts.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        fetchUserInfo()
        fetchCounts()
    }

    private fun fetchUserInfo() {
        viewModelScope.launch {
            userRepository.getUserInfo().collectLatest {
                _userInfo.value = it
            }
        }
    }

    private fun fetchCounts() {
        viewModelScope.launch {
            combine(
                userRepository.getUsersCount(),
                songRepository.getSongsAndEditorsChoiceCount(),
                categoryRepository.getCategoriesCount(),
                commonRepository.getCount(DATA.SLIDER_SHOW),
                commonRepository.getCount(DATA.ALBUMS),
                commonRepository.getCount(DATA.ARTISTS),
                songRepository.getFavoritesCount()
            ) { flows ->
                val users = flows[0] as Int
                val songsEC = flows[1] as Pair<Int, Int>
                val categories = flows[2] as Int
                val slider = flows[3] as Int
                val albums = flows[4] as Int
                val artists = flows[5] as Int
                val favorites = flows[6] as Int

                MainCounts(
                    users = users,
                    songs = songsEC.first,
                    editorsChoice = songsEC.second,
                    categories = categories,
                    sliderShow = slider,
                    albums = albums,
                    artists = artists,
                    favorites = favorites
                )
            }.collectLatest {
                _counts.value = it
                _isLoading.value = false
            }
        }
    }

    data class MainCounts(
        val users: Int = 0,
        val songs: Int = 0,
        val editorsChoice: Int = 0,
        val categories: Int = 0,
        val sliderShow: Int = 0,
        val albums: Int = 0,
        val artists: Int = 0,
        val favorites: Int = 0
    )
}
