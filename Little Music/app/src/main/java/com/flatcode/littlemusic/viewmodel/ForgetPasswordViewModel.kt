package com.flatcode.littlemusic.viewmodel

import android.util.Patterns
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ForgetPasswordViewModel @Inject constructor(
    private val auth: FirebaseAuth
) : BaseViewModel() {

    private val _forgetPasswordStatus = MutableStateFlow<ForgetPasswordStatus>(ForgetPasswordStatus.Idle)
    val forgetPasswordStatus: StateFlow<ForgetPasswordStatus> = _forgetPasswordStatus

    fun recoverPassword(email: String) {
        if (email.isEmpty()) {
            _forgetPasswordStatus.value = ForgetPasswordStatus.Error("Enter email...!")
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _forgetPasswordStatus.value = ForgetPasswordStatus.Error("Invalid email format...!")
            return
        }

        _forgetPasswordStatus.value = ForgetPasswordStatus.Loading("Sending password recovery instructions to $email")
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _forgetPasswordStatus.value = ForgetPasswordStatus.Success("Instructions to reset password sent to $email")
                } else {
                    _forgetPasswordStatus.value = ForgetPasswordStatus.Error(task.exception?.message ?: "Failed to send reset email")
                }
            }
            .addOnFailureListener { e ->
                _forgetPasswordStatus.value = ForgetPasswordStatus.Error(e.message ?: "Error occurred")
            }
    }

    sealed class ForgetPasswordStatus {
        object Idle : ForgetPasswordStatus()
        data class Loading(val message: String) : ForgetPasswordStatus()
        data class Success(val message: String) : ForgetPasswordStatus()
        data class Error(val message: String) : ForgetPasswordStatus()
    }
}