package com.example.nutrifit.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class EmailVerificationViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    sealed class VerificationState {
        object Idle : VerificationState()
        object Loading : VerificationState()
        object Success : VerificationState()
        data class Error(val message: String) : VerificationState()
    }

    private val _verificationState = MutableStateFlow<VerificationState>(VerificationState.Idle)
    val verificationState: StateFlow<VerificationState> = _verificationState

    fun checkVerificationStatus() {
        viewModelScope.launch {
            _verificationState.value = VerificationState.Loading
            try {
                auth.currentUser?.reload()?.await()
                if (auth.currentUser?.isEmailVerified == true) {
                    _verificationState.value = VerificationState.Success
                } else {
                    _verificationState.value = VerificationState.Error("Email chưa được xác thực.")
                }
            } catch (e: Exception) {
                _verificationState.value = VerificationState.Error("Lỗi kiểm tra: ${e.message}")
            }
        }
    }

    fun resendVerificationEmail(email: String) {
        viewModelScope.launch {
            _verificationState.value = VerificationState.Loading
            try {
                auth.currentUser?.sendEmailVerification()?.await()
                _verificationState.value = VerificationState.Idle // Reset sau resend
            } catch (e: Exception) {
                _verificationState.value = VerificationState.Error("Lỗi gửi email: ${e.message}")
            }
        }
    }

    fun resetState() {
        _verificationState.value = VerificationState.Idle
    }
}