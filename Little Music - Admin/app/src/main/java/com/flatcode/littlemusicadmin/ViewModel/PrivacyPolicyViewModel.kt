package com.flatcode.littlemusicadmin.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flatcode.littlemusicadmin.Repository.CommonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PrivacyPolicyViewModel @Inject constructor(
    private val repository: CommonRepository
) : ViewModel() {

    private val _privacyPolicy = MutableStateFlow("")
    val privacyPolicy: StateFlow<String> = _privacyPolicy.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        fetchPrivacyPolicy()
    }

    private fun fetchPrivacyPolicy() {
        viewModelScope.launch {
            repository.getPrivacyPolicy().collectLatest {
                _privacyPolicy.value = it
                _isLoading.value = false
            }
        }
    }
}
