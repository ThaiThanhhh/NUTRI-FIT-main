package com.example.nutrifit.ui.screens.register

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nutrifit.R
import com.example.nutrifit.theme.CornerRadius
import com.example.nutrifit.theme.NutriColor
import com.example.nutrifit.ui.components.CustomTextField
import com.example.nutrifit.ui.components.PasswordTextField
import com.example.nutrifit.ui.components.SocialLoginButton
import com.example.nutrifit.viewmodel.LoginViewModel
import com.example.nutrifit.viewmodel.RegisterViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

val GitHubButtonColor = Color(0xFF000000) // Màu GitHub (đen)
val GoogleButtonColor = Color(0xFF4285F4) // Màu Google

@Composable
fun RegisterScreen(
    onRegister: () -> Unit,
    onBackToLogin: () -> Unit,
    onEmailVerification: (String, String) -> Unit
) {
    val context = LocalContext.current
    val activity = context as android.app.Activity
    val socialAuthViewModel: LoginViewModel = viewModel()
    val registerViewModel: RegisterViewModel = viewModel() // ViewModel cho đăng ký email

    // --- Xử lý đăng nhập Social (Giữ nguyên) ---
    LaunchedEffect(Unit) {
        socialAuthViewModel.initGoogleSignIn(context)
    }
    val googleLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            socialAuthViewModel.handleGoogleSignInResult(result.data)
        }
    }
    val socialAuthState by socialAuthViewModel.authState.collectAsState()
    LaunchedEffect(socialAuthState) {
        when (socialAuthState) {
            is LoginViewModel.AuthState.Success -> {
                onRegister()
            }
            is LoginViewModel.AuthState.Error -> {
                Toast.makeText(context, (socialAuthState as LoginViewModel.AuthState.Error).message, Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }
    // --- Kết thúc xử lý đăng nhập Social ---

    // --- Xử lý đăng ký Email/Password với ViewModel ---
    val registrationState by registerViewModel.registrationState.collectAsState()
    var isLoading by remember { mutableStateOf(false) }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(registrationState) {
        when (val state = registrationState) {
            is RegisterViewModel.RegistrationState.Success -> {
                isLoading = false
                // Navigate đến EmailVerificationScreen với email và source
                onEmailVerification(email, "register")
                registerViewModel.resetState()
            }
            is RegisterViewModel.RegistrationState.Error -> {
                isLoading = false
                // Hiển thị thông báo lỗi trực tiếp từ ViewModel
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                registerViewModel.resetState()
            }
            is RegisterViewModel.RegistrationState.Loading -> {
                isLoading = true
            }
            is RegisterViewModel.RegistrationState.Idle -> {
                isLoading = false
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.loginbackground),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = androidx.compose.ui.layout.ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .clip(RoundedCornerShape(CornerRadius))
                    .background(Color.White.copy(alpha = 0.9f))
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(horizontal = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    HeaderSection(onBackToLogin = onBackToLogin)
                    Spacer(modifier = Modifier.height(16.dp))
                    LogoSection()
                    Spacer(modifier = Modifier.height(20.dp))
                    RegisterForm(
                        email = email,
                        onEmailChange = { email = it },
                        password = password,
                        onPasswordChange = { password = it },
                        confirmPassword = confirmPassword,
                        onConfirmPasswordChange = { confirmPassword = it },
                        rememberMe = rememberMe,
                        onRememberMeChange = { rememberMe = it },
                        focusManager = focusManager,
                        onRegisterClick = {
                            // Logic validation đã được chuyển vào ViewModel.
                            // UI chỉ cần gọi phương thức của ViewModel.
                            registerViewModel.registerWithEmailAndPassword(email, password, confirmPassword)
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    SocialLoginSection(
                        onGoogleLogin = { googleLauncher.launch(socialAuthViewModel.getGoogleSignInIntent()) },
                        onGitHubLogin = { socialAuthViewModel.signInWithGitHub(activity) }
                    )
                }
            }
        }

        // Overlay Loading
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable(enabled = false, onClick = {}),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = NutriColor)
            }
        }
    }
}

@Composable
fun HeaderSection(onBackToLogin: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        var isBackPressed by remember { mutableStateOf(false) }
        val backScale by animateFloatAsState(
            targetValue = if (isBackPressed) 0.9f else 1f,
            animationSpec = tween(100),
            label = "back_button_scale"
        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            modifier = Modifier
                .size(24.dp)
                .scale(backScale)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    isBackPressed = true
                    onBackToLogin()
                    // Coroutine Scope an toàn hơn
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(100)
                        isBackPressed = false
                    }
                },
            tint = Color.Black
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = "Đăng nhập", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.Black)
    }
}

@Composable
fun LogoSection() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "NUTRI-FIT Logo",
            modifier = Modifier.size(60.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            buildAnnotatedString {
                withStyle(style = SpanStyle(color = NutriColor, fontWeight = FontWeight.Bold, fontSize = 24.sp)) { append("NUTRI") }
                append(" - ")
                withStyle(style = SpanStyle(color = Color(0xFFFF0004), fontWeight = FontWeight.Bold, fontSize = 24.sp)) { append("FIT") }
            }
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = "Đăng ký để bắt đầu hành trình tập luyện", textAlign = TextAlign.Center, fontSize = 14.sp, color = Color.Gray, lineHeight = 18.sp)
        Text(text = "và ăn uống khoa học", textAlign = TextAlign.Center, fontSize = 14.sp, color = Color.Gray, lineHeight = 18.sp)
    }
}

@Composable
fun RegisterForm(
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    confirmPassword: String,
    onConfirmPasswordChange: (String) -> Unit,
    rememberMe: Boolean,
    onRememberMeChange: (Boolean) -> Unit,
    focusManager: FocusManager,
    onRegisterClick: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val bringIntoViewRequester = remember { BringIntoViewRequester() }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = "Email", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.Black, modifier = Modifier.padding(bottom = 4.dp))
        CustomTextField(
            value = email,
            onValueChange = onEmailChange,
            placeholder = "Nhập email",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            focusManager = focusManager,
            modifier = Modifier.onFocusEvent {
                if (it.isFocused) {
                    scope.launch {
                        delay(200)
                        bringIntoViewRequester.bringIntoView()
                    }
                }
            }
        )
        Spacer(modifier = Modifier.height(12.dp))

        Text(text = "Mật khẩu", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.Black, modifier = Modifier.padding(bottom = 4.dp))
        PasswordTextField(
            value = password,
            onValueChange = onPasswordChange,
            placeholder = "Nhập mật khẩu",
            focusManager = focusManager,
            modifier = Modifier.onFocusEvent {
                if (it.isFocused) {
                    scope.launch {
                        delay(200)
                        bringIntoViewRequester.bringIntoView()
                    }
                }
            }
        )
        Spacer(modifier = Modifier.height(12.dp))

        Text(text = "Xác nhận mật khẩu", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.Black, modifier = Modifier.padding(bottom = 4.dp))
        PasswordTextField(
            value = confirmPassword,
            onValueChange = onConfirmPasswordChange,
            placeholder = "Nhập lại mật khẩu",
            focusManager = focusManager,
            modifier = Modifier.onFocusEvent {
                if (it.isFocused) {
                    scope.launch {
                        delay(200)
                        bringIntoViewRequester.bringIntoView()
                    }
                }
            }
        )
        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = rememberMe, onCheckedChange = onRememberMeChange, modifier = Modifier.size(18.dp))
            Text(text = "Lưu mật khẩu", fontSize = 14.sp, color = Color.Gray, modifier = Modifier.padding(start = 8.dp))
        }
        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onRegisterClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .bringIntoViewRequester(bringIntoViewRequester),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = NutriColor)
        ) {
            Text(text = "Đăng ký", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}

@Composable
fun SocialLoginSection(
    onGoogleLogin: () -> Unit,
    onGitHubLogin: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SocialLoginButton(icon = R.drawable.google, text = "Đăng nhập với Google", onClick = onGoogleLogin, buttonColor = GoogleButtonColor)
        SocialLoginButton(icon = R.drawable.github, text = "Đăng nhập với GitHub", onClick = onGitHubLogin, buttonColor = GitHubButtonColor)
    }
}
