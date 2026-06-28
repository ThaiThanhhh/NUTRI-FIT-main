// Location: viewmodel/ForgotPasswordViewModel.kt
package com.example.nutrifit.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutrifit.data.repository.ForgotPasswordRepository
import com.example.nutrifit.data.repository.ForgotPasswordRepositoryImpl
import com.example.nutrifit.data.repository.ForgotPasswordResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ForgotPasswordUiState(
    val email: String = "",
    val isLoading: Boolean = false,
    val emailSent: Boolean = false,
    val errorMessage: String? = null,
    val resendCountdown: Int = 0, // Countdown timer để chống spam
    val canResend: Boolean = true
)

class ForgotPasswordViewModel(
    private val repository: ForgotPasswordRepository = ForgotPasswordRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ForgotPasswordUiState())
    val uiState: StateFlow<ForgotPasswordUiState> = _uiState.asStateFlow()

    private var lastSendTime: Long = 0
    private val MIN_RESEND_INTERVAL = 60_000L // 60 giây giữa các lần gửi

    fun onEmailChange(email: String) {
        _uiState.value = _uiState.value.copy(
            email = email,
            errorMessage = null
        )
    }

    fun sendPasswordResetEmail() {
        val email = _uiState.value.email.trim()

        // Validation
        if (email.isBlank()) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Vui lòng nhập email"
            )
            return
        }

        if (!isValidEmail(email)) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Email không hợp lệ"
            )
            return
        }

        // Check rate limiting
        val currentTime = System.currentTimeMillis()
        val timeSinceLastSend = currentTime - lastSendTime

        if (timeSinceLastSend < MIN_RESEND_INTERVAL && lastSendTime != 0L) {
            val remainingSeconds = ((MIN_RESEND_INTERVAL - timeSinceLastSend) / 1000).toInt()
            _uiState.value = _uiState.value.copy(
                errorMessage = "Vui lòng đợi $remainingSeconds giây trước khi gửi lại"
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            when (val result = repository.sendPasswordResetEmail(email)) {
                is ForgotPasswordResult.Success -> {
                    lastSendTime = System.currentTimeMillis()
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        emailSent = true,
                        canResend = false
                    )
                    startResendCountdown()
                }
                is ForgotPasswordResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
            }
        }
    }

    private fun startResendCountdown() {
        viewModelScope.launch {
            for (i in 60 downTo 1) {
                _uiState.value = _uiState.value.copy(
                    resendCountdown = i,
                    canResend = false
                )
                delay(1000)
            }
            _uiState.value = _uiState.value.copy(
                resendCountdown = 0,
                canResend = true
            )
        }
    }

    fun resetToEmailInput() {
        _uiState.value = _uiState.value.copy(
            emailSent = false,
            errorMessage = null,
            resendCountdown = 0
        )
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        return emailRegex.matches(email)
    }
}