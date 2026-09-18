package com.flatcode.littlemusic.ui.settings

import androidx.lifecycle.viewModelScope
import com.flatcode.littlemusic.repository.ToolsRepository
import com.flatcode.littlemusic.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PrivacyPolicyViewModel @Inject constructor(
    private val toolsRepository: ToolsRepository
) : BaseViewModel() {

    private val _privacyPolicy = MutableStateFlow("")
    val privacyPolicy: StateFlow<String> = _privacyPolicy

    fun loadPrivacyPolicy() {
        viewModelScope.launch {
            toolsRepository.getPrivacyPolicy().collect { policy ->
                _privacyPolicy.value = policy
            }
        }
    }
}