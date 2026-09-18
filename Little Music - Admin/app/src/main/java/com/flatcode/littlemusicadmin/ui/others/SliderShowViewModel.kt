package com.flatcode.littlemusicadmin.ui.others

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flatcode.littlemusicadmin.repository.SongRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SliderShowViewModel @Inject constructor(
    private val repository: SongRepository
) : ViewModel() {

    private val _sliders = MutableStateFlow<Map<String, String>>(emptyMap())
    val sliders: StateFlow<Map<String, String>> = _sliders.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        fetchSliders()
    }

    private fun fetchSliders() {
        viewModelScope.launch {
            repository.getSliderShow().collectLatest {
                _sliders.value = it
                _isLoading.value = false
            }
        }
    }
}
