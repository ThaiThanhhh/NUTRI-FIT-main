package com.example.nutrifit.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutrifit.utils.InputValidator
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class RegisterViewModel(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

    sealed class RegistrationState {
        object Idle : RegistrationState()
        object Loading : RegistrationState()
        object Success : RegistrationState()  // Thành công: đã tạo user và gửi email verification
        data class Error(val message: String) : RegistrationState()
    }

    private val _registrationState = MutableStateFlow<RegistrationState>(RegistrationState.Idle)
    val registrationState: StateFlow<RegistrationState> = _registrationState

    fun registerWithEmailAndPassword(email: String, password: String, confirmPassword: String) {
        // --- VALIDATION START ---
        if (!InputValidator.isFieldNotEmpty(email) || !InputValidator.isFieldNotEmpty(password) || !InputValidator.isFieldNotEmpty(confirmPassword)) {
            _registrationState.value = RegistrationState.Error("Vui lòng nhập đầy đủ thông tin")
            return
        }
        if (!InputValidator.isValidEmail(email)) {
            _registrationState.value = RegistrationState.Error("Định dạng email không hợp lệ")
            return
        }
        if (password != confirmPassword) {
            _registrationState.value = RegistrationState.Error("Mật khẩu và xác nhận mật khẩu không khớp")
            return
        }
        if (!InputValidator.isValidPassword(password)) {
            _registrationState.value = RegistrationState.Error("Mật khẩu phải có ít nhất 6 ký tự")
            return
        }
        // --- VALIDATION END ---

        viewModelScope.launch {
            _registrationState.value = RegistrationState.Loading
            try {
                // Tạo user
                auth.createUserWithEmailAndPassword(email, password).await()

                // Gửi email xác minh (quan trọng: Firebase không tự động làm)
                auth.currentUser?.sendEmailVerification()?.await()

                _registrationState.value = RegistrationState.Success
            } catch (e: Exception) {
                val errorMessage = when {
                    e.message?.contains("email-already-in-use", ignoreCase = true) == true ->
                        "Email này đã được sử dụng"
                    // Thông báo lỗi "weak-password" và "invalid-email" giờ đã được xử lý bởi validator
                    e.message?.contains("network", ignoreCase = true) == true ->
                        "Lỗi kết nối, vui lòng thử lại"
                    else -> "Có lỗi xảy ra: ${e.localizedMessage ?: "Vui lòng thử lại"}"
                }
                _registrationState.value = RegistrationState.Error(errorMessage)
            }
        }
    }

    fun resetState() {
        _registrationState.value = RegistrationState.Idle
    }
}
