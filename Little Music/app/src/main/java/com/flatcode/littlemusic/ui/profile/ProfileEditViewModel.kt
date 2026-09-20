package com.flatcode.littlemusic.ui.profile

import android.content.Context
import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.flatcode.littlemusic.repository.UserRepository
import com.flatcode.littlemusic.utils.DATA
import com.flatcode.littlemusic.utils.getFileExtension
import com.flatcode.littlemusic.ui.BaseViewModel
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Objects
import javax.inject.Inject

@HiltViewModel
class ProfileEditViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val database: FirebaseDatabase
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
        
        MediaManager.get().upload(imageUri)
            .option("folder", "Images/Profile/")
            .option("public_id", DATA.FirebaseUserUid)
            .callback(object : UploadCallback {
                override fun onStart(requestId: String) {
                }

                override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {
                }

                override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                    val imageUrl = resultData["secure_url"] as String
                    saveToDatabase(name, imageUrl)
                }

                override fun onError(requestId: String, error: ErrorInfo) {
                    _updateStatus.value = UpdateStatus.Error("Failed to upload image: ${error.description}")
                }

                override fun onReschedule(requestId: String, error: ErrorInfo) {
                }
            }).dispatch()
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