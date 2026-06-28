// Location: ui/screens/forgotpw/ForgotPasswordScreen.kt
package com.example.nutrifit.ui.screens.forgotpw

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nutrifit.R
import com.example.nutrifit.viewmodel.ForgotPasswordViewModel

@Composable
fun ForgotPasswordScreen(
    modifier: Modifier = Modifier,
    onBackToLogin: () -> Unit = {},
    viewModel: ForgotPasswordViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(modifier = modifier.fillMaxSize()) {
        // Background image with scrim
        Image(
            painter = painterResource(R.drawable.rectangle_59),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(Color.Black.copy(alpha = 0.35f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Main card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .background(
                        color = Color.White.copy(alpha = 0.95f),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(24.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Logo section
                    LogoSection()

                    Spacer(modifier = Modifier.height(24.dp))

                    if (uiState.emailSent) {
                        // Success state
                        SuccessContent(
                            email = uiState.email,
                            onBackToLogin = onBackToLogin,
                            onResendEmail = { viewModel.resetToEmailInput() }
                        )
                    } else {
                        // Email input state
                        EmailInputContent(
                            email = uiState.email,
                            onEmailChange = { viewModel.onEmailChange(it) },
                            isLoading = uiState.isLoading,
                            errorMessage = uiState.errorMessage,
                            onSendEmail = { viewModel.sendPasswordResetEmail() },
                            onBackToLogin = onBackToLogin
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmailInputContent(
    email: String,
    onEmailChange: (String) -> Unit,
    isLoading: Boolean,
    errorMessage: String?,
    onSendEmail: () -> Unit,
    onBackToLogin: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Title
        Text(
            text = "Quên mật khẩu",
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Description
        Text(
            text = "Nhập email bạn đã dùng để đăng ký.\nChúng tôi sẽ gửi link đặt lại mật khẩu.",
            style = TextStyle(
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Email input
        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            placeholder = { Text("Nhập địa chỉ email") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !isLoading,
            isError = errorMessage != null,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                errorContainerColor = Color.Transparent,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            )
        )

        // Error message
        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = errorMessage,
                style = TextStyle(
                    fontSize = 12.sp,
                    color = Color(0xFFE53935)
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Send button
        Button(
            onClick = onSendEmail,
            enabled = email.isNotBlank() && !isLoading,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF56B141),
                disabledContainerColor = Color(0xFFBDBDBD)
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = "Gửi email khôi phục",
                    color = Color.White,
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Back to login
        Text(
            text = "Về đăng nhập",
            style = TextStyle(
                fontSize = 13.sp,
                color = Color(0xFF2196F3),
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier.clickable(enabled = !isLoading) { onBackToLogin() }
        )
    }
}

@Composable
private fun SuccessContent(
    email: String,
    onBackToLogin: () -> Unit,
    onResendEmail: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Success icon
        Text(
            text = "✓",
            style = TextStyle(
                fontSize = 64.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4CAF50)
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Title
        Text(
            text = "Email đã được gửi!",
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Description
        Text(
            text = "Chúng tôi đã gửi link đặt lại mật khẩu đến:",
            style = TextStyle(
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = email,
            style = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Vui lòng kiểm tra hộp thư (và thư mục spam nếu không thấy)",
            style = TextStyle(
                fontSize = 12.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Back to login button
        Button(
            onClick = onBackToLogin,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2196F3)
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text(
                text = "Về trang đăng nhập",
                color = Color.White,
                fontWeight = FontWeight.Medium,
                fontSize = 15.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Resend link
        Text(
            text = "Không nhận được email? Gửi lại",
            style = TextStyle(
                fontSize = 13.sp,
                color = Color(0xFF2196F3),
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier.clickable { onResendEmail() }
        )
    }
}

@Composable
private fun LogoSection() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Logo
        Image(
            painter = painterResource(R.drawable.logo),
            contentDescription = "NutriFit Logo",
            modifier = Modifier.size(80.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // App name
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "NUTRI",
                style = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1AC9AC)
                )
            )
            Text(
                text = " - ",
                style = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            )
            Text(
                text = "FIT",
                style = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFF0004)
                )
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Subtitle
        Text(
            text = "Khôi phục mật khẩu",
            style = TextStyle(
                fontSize = 12.sp,
                color = Color.Gray
            )
        )
    }
}