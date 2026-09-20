package com.flatcode.littlemusicadmin.ui.editorschoice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flatcode.littlemusicadmin.model.EditorsChoice
import com.flatcode.littlemusicadmin.repository.SongRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditorsChoiceViewModel @Inject constructor(
    private val repository: SongRepository
) : ViewModel() {

    private val _editorsChoiceList = MutableStateFlow<List<EditorsChoice>>(emptyList())
    val editorsChoiceList: StateFlow<List<EditorsChoice>> = _editorsChoiceList.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        fetchEditorsChoice()
    }

    private fun fetchEditorsChoice() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getEditorsChoiceSongs().collectLatest { songs ->
                val list = mutableListOf<EditorsChoice>()
                for (i in 1..50) {
                    val song = songs.find { it.editorsChoice == i }
                    list.add(EditorsChoice(i, song))
                }
                _editorsChoiceList.value = list
                _isLoading.value = false
            }
        }
    }
}
