package com.flatcode.littlemusicadmin.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flatcode.littlemusicadmin.Model.Song
import com.flatcode.littlemusicadmin.Repository.SongRepository
import com.flatcode.littlemusicadmin.Unit.DATA
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val repository: SongRepository
) : ViewModel() {

    private val _favorites = MutableStateFlow<List<Song>>(emptyList())
    val favorites: StateFlow<List<Song>> = _favorites.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _orderBy = MutableStateFlow(DATA.TIMESTAMP)
    val orderBy: StateFlow<String> = _orderBy.asStateFlow()

    init {
        fetchFavorites()
    }

    private fun fetchFavorites() {
        viewModelScope.launch {
            _orderBy.collectLatest { order ->
                _isLoading.value = true
                repository.getFavorites(order).collectLatest {
                    _favorites.value = it
                    _isLoading.value = false
                }
            }
        }
    }

    fun setOrderBy(order: String) {
        _orderBy.value = order
    }
}
