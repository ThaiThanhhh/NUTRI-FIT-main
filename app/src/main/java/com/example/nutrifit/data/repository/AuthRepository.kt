// File: data/repository/AuthRepository.kt
package com.example.nutrifit.data.repository

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.GithubAuthProvider
import kotlinx.coroutines.tasks.await

sealed class AuthState {
    data object Idle : AuthState()
    data object Loading : AuthState()
    data class Success(val user: FirebaseUser?) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthRepository {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser

    // Sign in with Google
    suspend fun signInWithGoogle(account: GoogleSignInAccount): Result<FirebaseUser> {
        return try {
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            val result = firebaseAuth.signInWithCredential(credential).await()
            result.user?.let {
                Result.success(it)
            } ?: Result.failure(Exception("Google sign in failed"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Sign in with GitHub
    suspend fun signInWithGitHub(token: String): Result<FirebaseUser> {
        return try {
            val credential = GithubAuthProvider.getCredential(token)
            val result = firebaseAuth.signInWithCredential(credential).await()
            result.user?.let {
                Result.success(it)
            } ?: Result.failure(Exception("GitHub sign in failed"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Sign out
    fun signOut() {
        firebaseAuth.signOut()
    }

    // Check if user is logged in
    fun isUserLoggedIn(): Boolean {
        return currentUser != null
    }

    companion object {
        // Singleton pattern thay cho Hilt
        @Volatile
        private var instance: AuthRepository? = null

        fun getInstance(): AuthRepository {
            return instance ?: synchronized(this) {
                instance ?: AuthRepository().also { instance = it }
            }
        }
    }
}