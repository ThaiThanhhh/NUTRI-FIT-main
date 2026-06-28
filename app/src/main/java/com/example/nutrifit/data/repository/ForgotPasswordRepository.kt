// Location: data/repository/ForgotPasswordRepository.kt
package com.example.nutrifit.data.repository

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

sealed class ForgotPasswordResult {
    object Success : ForgotPasswordResult()
    data class Error(val message: String) : ForgotPasswordResult()
}

interface ForgotPasswordRepository {
    suspend fun sendPasswordResetEmail(email: String): ForgotPasswordResult
}

class ForgotPasswordRepositoryImpl(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ForgotPasswordRepository {

    override suspend fun sendPasswordResetEmail(email: String): ForgotPasswordResult {
        return try {
            auth.sendPasswordResetEmail(email).await()
            ForgotPasswordResult.Success
        } catch (e: Exception) {
            val errorMessage = when {
                e.message?.contains("no user record", ignoreCase = true) == true ||
                        e.message?.contains("USER_NOT_FOUND", ignoreCase = true) == true ->
                    "Email này chưa được đăng ký"

                e.message?.contains("invalid-email", ignoreCase = true) == true ->
                    "Email không hợp lệ"

                e.message?.contains("network", ignoreCase = true) == true ->
                    "Lỗi kết nối, vui lòng thử lại"

                else -> "Có lỗi xảy ra: ${e.localizedMessage ?: "Vui lòng thử lại"}"
            }
            ForgotPasswordResult.Error(errorMessage)
        }
    }
}