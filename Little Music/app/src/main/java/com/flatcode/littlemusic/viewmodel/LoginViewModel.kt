package com.flatcode.littlemusic.viewmodel

import android.util.Patterns
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val auth: FirebaseAuth
) : BaseViewModel() {

    private val _loginStatus = MutableStateFlow<LoginStatus>(LoginStatus.Idle)
    val loginStatus: StateFlow<LoginStatus> = _loginStatus

    fun login(email: String, password: String) {
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _loginStatus.value = LoginStatus.Error("Invalid email pattern...!")
            return
        }
        if (password.isEmpty()) {
            _loginStatus.value = LoginStatus.Error("Enter password...!")
            return
        }

        _loginStatus.value = LoginStatus.Loading("Logging In...")
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                _loginStatus.value = LoginStatus.Success
            }
            .addOnFailureListener { e ->
                _loginStatus.value = LoginStatus.Error(e.message ?: "Login Failed")
            }
    }

    sealed class LoginStatus {
        object Idle : LoginStatus()
        data class Loading(val message: String) : LoginStatus()
        object Success : LoginStatus()
        data class Error(val message: String) : LoginStatus()
    }
}