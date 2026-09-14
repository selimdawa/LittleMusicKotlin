package com.flatcode.littlemusic.viewmodel

import androidx.lifecycle.viewModelScope
import com.flatcode.littlemusic.model.Category
import com.flatcode.littlemusic.repository.MusicRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoriesFragViewModel @Inject constructor(
    private val musicRepository: MusicRepository
) : BaseViewModel() {

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun loadCategories() {
        viewModelScope.launch {
            _isLoading.value = true
            musicRepository.getCategories().collect { list ->
                _categories.value = list
                _isLoading.value = false
            }
        }
    }
}