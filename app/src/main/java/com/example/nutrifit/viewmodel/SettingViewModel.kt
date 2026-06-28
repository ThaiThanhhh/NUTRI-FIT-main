package com.example.nutrifit.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutrifit.data.model.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

sealed class SettingUiState {
    data class Success(val user: User, val providers: List<String>) : SettingUiState()
    data class Error(val message: String) : SettingUiState()
    object Loading : SettingUiState()
}

class SettingViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<SettingUiState>(SettingUiState.Loading)
    val uiState: StateFlow<SettingUiState> = _uiState.asStateFlow()

    init {
        loadUserData()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            val firebaseAuthUser = Firebase.auth.currentUser
            if (firebaseAuthUser == null) {
                _uiState.value = SettingUiState.Error("User not logged in")
                return@launch
            }

            try {
                val uid = firebaseAuthUser.uid
                val firestore = Firebase.firestore
                
                // Lấy dữ liệu từ Firestore
                val userDocument = firestore.collection("users").document(uid).get().await()
                val userFromFirestore = userDocument.toObject<User>()

                if (userFromFirestore != null) {
                    // Lấy danh sách nhà cung cấp đăng nhập từ Auth
                    val providers = firebaseAuthUser.providerData.map { it.providerId }
                    _uiState.value = SettingUiState.Success(userFromFirestore, providers)
                } else {
                     _uiState.value = SettingUiState.Error("User data not found in Firestore.")
                }

            } catch (e: Exception) {
                _uiState.value = SettingUiState.Error(e.message ?: "An unknown error occurred")
            }
        }
    }
}
