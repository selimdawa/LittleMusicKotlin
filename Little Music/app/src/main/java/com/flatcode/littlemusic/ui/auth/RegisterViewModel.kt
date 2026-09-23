package com.flatcode.littlemusic.ui.auth

import android.util.Patterns
import com.flatcode.littlemusic.ui.BaseViewModel
import com.flatcode.littlemusic.utils.DATA
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val database: FirebaseDatabase
) : BaseViewModel() {

    private val _registerStatus = MutableStateFlow<RegisterStatus>(RegisterStatus.Idle)
    val registerStatus: StateFlow<RegisterStatus> = _registerStatus

    fun register(name: String, email: String, password: String, cPassword: String) {
        if (name.isEmpty()) {
            _registerStatus.value = RegisterStatus.Error("Enter your name...")
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _registerStatus.value = RegisterStatus.Error("Invalid email pattern...!")
            return
        }
        if (password.isEmpty()) {
            _registerStatus.value = RegisterStatus.Error("Enter password...!")
            return
        }
        if (cPassword.isEmpty()) {
            _registerStatus.value = RegisterStatus.Error("Confirm Password...!")
            return
        }
        if (password != cPassword) {
            _registerStatus.value = RegisterStatus.Error("Password doesn't match...!")
            return
        }

        _registerStatus.value = RegisterStatus.Loading("Creating account...")
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                updateUserInfo(name, email, auth.uid)
            }
            .addOnFailureListener { e ->
                _registerStatus.value = RegisterStatus.Error(e.message ?: "Registration Failed")
            }
    }

    private fun updateUserInfo(name: String, email: String, id: String?) {
        if (id == null) {
            _registerStatus.value = RegisterStatus.Error("User ID is null")
            return
        }
        _registerStatus.value = RegisterStatus.Loading("Saving user info...")
        val hashMap = HashMap<String, Any>()
        hashMap[DATA.EMAIL] = email
        hashMap[DATA.ID] = id
        hashMap[DATA.PROFILE_IMAGE] = DATA.BASIC
        hashMap[DATA.TIMESTAMP] = System.currentTimeMillis()
        hashMap[DATA.USER_NAME] = name
        hashMap[DATA.VERSION] = DATA.CURRENT_VERSION

        database.getReference(DATA.USERS).child(id).setValue(hashMap)
            .addOnSuccessListener {
                _registerStatus.value = RegisterStatus.Success
            }
            .addOnFailureListener { e ->
                _registerStatus.value =
                    RegisterStatus.Error(e.message ?: "Failed to save user info")
            }
    }

    sealed class RegisterStatus {
        object Idle : RegisterStatus()
        data class Loading(val message: String) : RegisterStatus()
        object Success : RegisterStatus()
        data class Error(val message: String) : RegisterStatus()
    }
}