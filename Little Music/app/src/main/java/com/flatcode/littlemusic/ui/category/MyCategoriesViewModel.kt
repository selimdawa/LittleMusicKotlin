package com.flatcode.littlemusic.ui.category

import androidx.lifecycle.viewModelScope
import com.flatcode.littlemusic.model.Category
import com.flatcode.littlemusic.repository.MusicRepository
import com.flatcode.littlemusic.utils.DATA
import com.flatcode.littlemusic.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyCategoriesViewModel @Inject constructor(
    private val musicRepository: MusicRepository
) : BaseViewModel() {

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _currentType = MutableStateFlow(DATA.TIMESTAMP)
    val currentType: StateFlow<String> = _currentType

    fun setType(type: String) {
        _currentType.value = type
        getData()
    }

    fun getData() {
        viewModelScope.launch {
            _isLoading.value = true
            musicRepository.getInterestedIds(DATA.FirebaseUserUid, DATA.CATEGORIES).collect { interestedIds ->
                musicRepository.getCategories().collect { allCategories ->
                    _categories.value = allCategories.filter { it.id in interestedIds }
                    _isLoading.value = false
                }
            }
        }
    }
}