package com.flatcode.littlemusic.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.flatcode.littlemusic.repository.UserRepository
import com.flatcode.littlemusic.utils.DATA
import com.flatcode.littlemusic.utils.VOID
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Objects
import javax.inject.Inject

@HiltViewModel
class ProfileEditViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val database: FirebaseDatabase,
    private val storage: FirebaseStorage
) : BaseViewModel() {

    private val _username = MutableStateFlow("")
    val username: StateFlow<String> = _username

    private val _profileImage = MutableStateFlow("")
    val profileImage: StateFlow<String> = _profileImage

    private val _updateStatus = MutableStateFlow<UpdateStatus>(UpdateStatus.Idle)
    val updateStatus: StateFlow<UpdateStatus> = _updateStatus

    fun loadUserInfo() {
        viewModelScope.launch {
            userRepository.getUserInfo(DATA.FirebaseUserUid).collect { user ->
                user?.let {
                    _username.value = it.username ?: ""
                    _profileImage.value = it.profileImage ?: ""
                }
            }
        }
    }

    fun updateProfile(name: String, imageUri: Uri?, context: Context) {
        if (name.isBlank()) {
            _updateStatus.value = UpdateStatus.Error("Enter name...")
            return
        }

        if (imageUri == null) {
            saveToDatabase(name, null)
        } else {
            uploadImage(name, imageUri, context)
        }
    }

    private fun uploadImage(name: String, imageUri: Uri, context: Context) {
        _updateStatus.value = UpdateStatus.Loading("Uploading Image...")
        val filePathAndName = "Images/Profile/" + DATA.FirebaseUserUid
        val extension = VOID.getFileExtension(imageUri, context)
        val reference = storage.getReference("$filePathAndName.$extension")
        
        reference.putFile(imageUri)
            .addOnSuccessListener { taskSnapshot ->
                taskSnapshot.storage.downloadUrl.addOnSuccessListener { uri ->
                    saveToDatabase(name, uri.toString())
                }.addOnFailureListener { e ->
                    _updateStatus.value = UpdateStatus.Error("Failed to get download URL: ${e.message}")
                }
            }.addOnFailureListener { e ->
                _updateStatus.value = UpdateStatus.Error("Failed to upload image: ${e.message}")
            }
    }

    private fun saveToDatabase(name: String, imageUrl: String?) {
        _updateStatus.value = UpdateStatus.Loading("Updating user profile...")
        val hashMap = HashMap<String, Any>()
        hashMap[DATA.USER_NAME] = name
        if (imageUrl != null) {
            hashMap[DATA.PROFILE_IMAGE] = imageUrl
        }
        
        val reference = database.getReference(DATA.USERS)
        reference.child(Objects.requireNonNull(DATA.FirebaseUserUid)).updateChildren(hashMap)
            .addOnSuccessListener {
                _updateStatus.value = UpdateStatus.Success("Profile updated...")
            }.addOnFailureListener { e ->
                _updateStatus.value = UpdateStatus.Error("Failed to update database: ${e.message}")
            }
    }

    sealed class UpdateStatus {
        object Idle : UpdateStatus()
        data class Loading(val message: String) : UpdateStatus()
        data class Success(val message: String) : UpdateStatus()
        data class Error(val message: String) : UpdateStatus()
    }
}