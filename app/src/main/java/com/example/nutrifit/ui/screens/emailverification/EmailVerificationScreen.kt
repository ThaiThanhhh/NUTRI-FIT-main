package com.example.nutrifit.ui.screens.emailverification

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nutrifit.R
import com.example.nutrifit.viewmodel.EmailVerificationViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val NutriColor = Color(0xFF1AC9AC)
private val CornerRadius = 16.dp

@Composable
fun EmailVerificationScreen(
    email: String,
    source: String,
    onVerificationSuccess: () -> Unit,
    onBackToLogin: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: EmailVerificationViewModel = viewModel()
    val verificationState by viewModel.verificationState.collectAsState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(verificationState) {
        when (val state = verificationState) {
            is EmailVerificationViewModel.VerificationState.Success -> {
                Toast.makeText(context, "Email đã được xác thực!", Toast.LENGTH_SHORT).show()
                onVerificationSuccess()
                viewModel.resetState()
            }
            is EmailVerificationViewModel.VerificationState.Error -> {
                Toast.makeText(context, "Lỗi: ${state.message}", Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.loginbackground),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .align(Alignment.Center)
                .padding(horizontal = 20.dp)
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
                    Icon(
                        imageVector = Icons.Filled.Email,
                        contentDescription = "Email Icon",
                        modifier = Modifier.size(60.dp),
                        tint = NutriColor
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Xác thực Email",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Chúng tôi đã gửi email xác thực đến $email. Vui lòng kiểm tra hộp thư và nhấn vào liên kết để xác thực tài khoản.",
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp,
                        color = Color.Gray,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = { viewModel.checkVerificationStatus() },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NutriColor)
                    ) {
                        Text("Tôi đã xác thực", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Gửi lại email",
                        fontSize = 14.sp,
                        color = NutriColor,
                        modifier = Modifier.clickable {
                            scope.launch {
                                viewModel.resendVerificationEmail(email)
                                delay(1000) // Debounce
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Quay về đăng nhập",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.clickable { onBackToLogin() }
                    )
                }
            }
        }
    }
}