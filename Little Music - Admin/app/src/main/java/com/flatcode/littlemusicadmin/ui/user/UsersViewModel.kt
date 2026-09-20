package com.flatcode.littlemusicadmin.ui.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flatcode.littlemusicadmin.model.User
import com.flatcode.littlemusicadmin.repository.UserRepository
import com.flatcode.littlemusicadmin.utils.DATA
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UsersViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {

    private val _users = MutableStateFlow<List<User>>(emptyList())
    private val _searchQuery = MutableStateFlow("")

    val users: StateFlow<List<User>> = combine(_users, _searchQuery) { users, query ->
        if (query.isEmpty()) {
            users
        } else {
            users.filter {
                it.username?.contains(query, ignoreCase = true) == true
            }
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _orderBy = MutableStateFlow(DATA.TIMESTAMP)
    val orderBy: StateFlow<String> = _orderBy.asStateFlow()

    init {
        fetchUsers()
    }

    private fun fetchUsers() {
        viewModelScope.launch {
            _orderBy.collectLatest { order ->
                _isLoading.value = true
                repository.getUsers(order).collectLatest {
                    _users.value = it
                    _isLoading.value = false
                }
            }
        }
    }

    fun setOrderBy(order: String) {
        _orderBy.value = order
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }
}
