package com.example.nutrifit.viewmodel

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutrifit.data.repository.UserRepository
import com.example.nutrifit.utils.InputValidator
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.OAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Suppress("DEPRECATION")
class LoginViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val userRepository = UserRepository()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    sealed class NextScreen {
        object Profile : NextScreen()
        object Target : NextScreen()
        object Home : NextScreen()
    }

    sealed class AuthState {
        object Idle : AuthState()
        object Loading : AuthState()
        data class Success(val user: FirebaseUser, val nextScreen: NextScreen) : AuthState()
        data class EmailNotVerified(val email: String) : AuthState()
        data class Error(val message: String) : AuthState()
    }

    private lateinit var googleSignInClient: GoogleSignInClient

    fun initGoogleSignIn(context: Context) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("500918832385-enst8igc1g6e32fh8k4nabulaertp2dp.apps.googleusercontent.com")
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(context, gso)
    }

    fun getGoogleSignInIntent(): Intent {
        googleSignInClient.signOut()
        return googleSignInClient.signInIntent
    }

    fun signInWithEmail(email: String, password: String) {
        // --- VALIDATION START ---
        if (!InputValidator.isFieldNotEmpty(email) || !InputValidator.isFieldNotEmpty(password)) {
            _authState.value = AuthState.Error("Vui lòng nhập đầy đủ thông tin")
            return
        }
        if (!InputValidator.isValidEmail(email)) {
            _authState.value = AuthState.Error("Định dạng email không hợp lệ")
            return
        }
        // --- VALIDATION END ---

        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val result = auth.signInWithEmailAndPassword(email, password).await()
                val user = result.user

                if (user == null) {
                    _authState.value = AuthState.Error("Đăng nhập thất bại. Vui lòng thử lại.")
                    return@launch
                }

                if (!user.isEmailVerified) {
                    auth.signOut()
                    _authState.value = AuthState.EmailNotVerified(email)
                    return@launch
                }

                checkNewUser(user)

            } catch (e: Exception) {
                val errorMessage = when (e) {
                    is FirebaseAuthInvalidCredentialsException -> "Email hoặc mật khẩu không đúng"
                    is FirebaseAuthInvalidUserException -> "Tài khoản không tồn tại hoặc đã bị vô hiệu hóa"
                    else -> e.message ?: "Đăng nhập thất bại. Vui lòng thử lại."
                }
                _authState.value = AuthState.Error(errorMessage)
            }
        }
    }

    fun handleGoogleSignInResult(data: Intent?) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                _authState.value = AuthState.Error("Đăng nhập Google thất bại: ${e.message}")
            }
        }
    }

    private suspend fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        try {
            val result = auth.signInWithCredential(credential).await()
            result.user?.let { user ->
                checkNewUser(user)
            } ?: run {
                _authState.value = AuthState.Error("Xác thực Firebase thất bại")
            }
        } catch (e: Exception) {
            _authState.value = AuthState.Error(e.message ?: "Xác thực Firebase thất bại")
        }
    }

    fun signInWithGitHub(activity: Activity) {
        _authState.value = AuthState.Loading
        auth.signOut()

        val provider = OAuthProvider.newBuilder("github.com")
            .addCustomParameter("prompt", "login")
            .build()

        auth.startActivityForSignInWithProvider(activity, provider)
            .addOnSuccessListener { authResult ->
                viewModelScope.launch {
                    authResult.user?.let { user ->
                        checkNewUser(user)
                    } ?: run {
                        _authState.value = AuthState.Error("Đăng nhập GitHub thất bại")
                    }
                }
            }
            .addOnFailureListener { e ->
                viewModelScope.launch {
                    _authState.value = AuthState.Error("Đăng nhập GitHub thất bại: ${e.message}")
                }
            }
    }

    private suspend fun checkNewUser(user: FirebaseUser) {
        try {
            val userData = userRepository.getUser(user.uid)
            val nextScreen = if (userData.isSuccess) {
                val userInfo = userData.getOrNull()
                when {
                    userInfo?.name.isNullOrBlank() -> NextScreen.Profile
                    userInfo?.goal.isNullOrBlank() -> NextScreen.Target
                    else -> NextScreen.Home
                }
            } else {
                NextScreen.Profile
            }
            _authState.value = AuthState.Success(user, nextScreen)
        } catch (e: Exception) {
            _authState.value = AuthState.Success(user, NextScreen.Profile)
        }
    }

    fun signOut() {
        auth.signOut()
        if (::googleSignInClient.isInitialized) {
            googleSignInClient.signOut()
        }
        _authState.value = AuthState.Idle
    }

    fun resetAuthState() {
        _authState.value = AuthState.Idle
    }
}
