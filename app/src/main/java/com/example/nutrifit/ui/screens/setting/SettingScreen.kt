package com.example.nutrifit.ui.screens.setting

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.nutrifit.MainApplication
import com.example.nutrifit.R
import com.example.nutrifit.ui.navigation.NavRoutes
import com.example.nutrifit.viewmodel.SettingUiState
import com.example.nutrifit.viewmodel.SettingViewModel
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    onBackClick: () -> Unit = {},
    navController: NavController? = null,
    viewModel: SettingViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val sharedPreferences = remember { context.getSharedPreferences("notification_prefs", Context.MODE_PRIVATE) }
    var notificationsEnabled by remember {
        mutableStateOf(sharedPreferences.getBoolean("notifications_enabled", false))
    }
    var showBatteryInfoDialog by remember { mutableStateOf(false) }

    if (showBatteryInfoDialog) {
        AlertDialog(
            onDismissRequest = { showBatteryInfoDialog = false },
            title = { Text("Lưu ý Quan Trọng", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Để đảm bảo nhận thông báo đúng giờ, vui lòng kiểm tra Cài đặt Pin trên điện thoại:")
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("∙ Tắt 'Quản lý ứng dụng nếu không dùng' (hoặc tương tự).", lineHeight = 20.sp)
                    Text("∙ Bật 'Cho phép hoạt động dưới nền'.", lineHeight = 20.sp)
                }
            },
            confirmButton = {
                Button(onClick = { showBatteryInfoDialog = false }) {
                    Text("Đã hiểu")
                }
            }
        )
    }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                notificationsEnabled = true
                with(sharedPreferences.edit()) {
                    putBoolean("notifications_enabled", true)
                    apply()
                }
                MainApplication.scheduleAllDailyReminders(context)
                Toast.makeText(context, "Đã bật thông báo.", Toast.LENGTH_SHORT).show()
                showBatteryInfoDialog = true
            } else {
                Toast.makeText(context, "Bạn cần cấp quyền để nhận thông báo.", Toast.LENGTH_LONG).show()
            }
        }
    )

    fun handleNotificationSwitchChange(isEnabled: Boolean) {
        if (isEnabled) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                when (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)) {
                    PackageManager.PERMISSION_GRANTED -> {
                        notificationsEnabled = true
                        with(sharedPreferences.edit()) {
                            putBoolean("notifications_enabled", true)
                            apply()
                        }
                        MainApplication.scheduleAllDailyReminders(context)
                        Toast.makeText(context, "Đã bật thông báo.", Toast.LENGTH_SHORT).show()
                        showBatteryInfoDialog = true
                    }
                    else -> {
                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
            } else {
                notificationsEnabled = true
                with(sharedPreferences.edit()) {
                    putBoolean("notifications_enabled", true)
                    apply()
                }
                MainApplication.scheduleAllDailyReminders(context)
                Toast.makeText(context, "Đã bật thông báo.", Toast.LENGTH_SHORT).show()
                showBatteryInfoDialog = true
            }
        } else {
            notificationsEnabled = false
            with(sharedPreferences.edit()) {
                putBoolean("notifications_enabled", false)
                apply()
            }
            MainApplication.cancelAllDailyReminders(context)
            Toast.makeText(context, "Đã tắt thông báo.", Toast.LENGTH_SHORT).show()
        }
    }

    when (val state = uiState) {
        is SettingUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is SettingUiState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Error: ${state.message}")
            }
        }
        is SettingUiState.Success -> {
            val user = state.user
            val providers = state.providers
            val height by remember { mutableStateOf(user.height ?: "") }
            val weight by remember { mutableStateOf(user.weight ?: "") }
            val goal by remember { mutableStateOf(user.goal ?: "") }

            Scaffold(
                content = { paddingValues ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFFF5F5F5))
                            .padding(paddingValues)
                    ) {
                        // Top Banner / Header
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(90.dp)
                                .padding(16.dp)
                                .shadow(
                                    elevation = 8.dp,
                                    shape = RoundedCornerShape(16.dp),
                                    clip = true
                                ),
                            color = Color(0xFF4F66FF),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                // Back Button
                                IconButton(
                                    onClick = onBackClick,
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        Icons.Default.ArrowBack,
                                        contentDescription = "Back",
                                        tint = Color.White
                                    )
                                }

                                // Title and Subtitle
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = "Cài đặt tài khoản",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "Quản lí thông tin cá nhân",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.White.copy(alpha = 0.8f)
                                    )
                                }

                                // Empty space for balance
                                Spacer(modifier = Modifier.size(24.dp))
                            }
                        }

                        // Main Content with Vertical Scroll
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(WindowInsets.statusBars.asPaddingValues())
                        ) {
                            // PHẦN 1: THÔNG TIN CÁ NHÂN
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                                    .shadow(
                                        elevation = 4.dp,
                                        shape = RoundedCornerShape(16.dp)
                                    ),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Column(
                                    modifier = Modifier.padding(20.dp)
                                ) {
                                    // Section Header
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 16.dp)
                                            .background(
                                                Color(0xFF4F66FF).copy(alpha = 0.1f),
                                                RoundedCornerShape(8.dp)
                                            )
                                    ) {
                                        Text(
                                            text = "1. Thông tin cá nhân",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF4F66FF),
                                            modifier = Modifier
                                                .padding(vertical = 8.dp, horizontal = 16.dp)
                                                .fillMaxWidth(),
                                            textAlign = TextAlign.Center
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Column(
                                        verticalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        // Họ và tên Field
                                        Column {
                                            Text(
                                                text = "Họ và tên",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = Color.Black,
                                                modifier = Modifier.padding(bottom = 4.dp)
                                            )
                                            OutlinedTextField(
                                                value = user.name,
                                                onValueChange = { },
                                                modifier = Modifier.fillMaxWidth(),
                                                placeholder = { Text("Nhập họ tên...") },
                                                enabled = false,
                                                shape = RoundedCornerShape(12.dp),
                                                colors = OutlinedTextFieldDefaults.colors(
                                                    disabledTextColor = Color.Black,
                                                    disabledBorderColor = Color.Gray.copy(alpha = 0.5f)
                                                )
                                            )
                                        }

                                        // Email Field
                                        Column {
                                            Text(
                                                text = "Email",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = Color.Black,
                                                modifier = Modifier.padding(bottom = 4.dp)
                                            )
                                            OutlinedTextField(
                                                value = user.email,
                                                onValueChange = { },
                                                modifier = Modifier.fillMaxWidth(),
                                                placeholder = { Text("Nhập email...") },
                                                enabled = false,
                                                shape = RoundedCornerShape(12.dp),
                                                colors = OutlinedTextFieldDefaults.colors(
                                                    disabledTextColor = Color.Black,
                                                    disabledBorderColor = Color.Gray.copy(alpha = 0.5f)
                                                )
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(24.dp))

                                    // Divider
                                    Divider(
                                        color = Color.LightGray.copy(alpha = 0.5f),
                                        thickness = 1.dp
                                    )

                                    Spacer(modifier = Modifier.height(24.dp))

                                    Column(
                                        verticalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        // Height Field
                                        Column {
                                            Text(
                                                text = "Chiều cao",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = Color.Black,
                                                modifier = Modifier.padding(bottom = 4.dp)
                                            )
                                            OutlinedTextField(
                                                value = height,
                                                onValueChange = { },
                                                modifier = Modifier.fillMaxWidth(),
                                                enabled = false,
                                                shape = RoundedCornerShape(12.dp),
                                                colors = OutlinedTextFieldDefaults.colors(
                                                    disabledTextColor = Color.Black,
                                                    disabledBorderColor = Color.Gray.copy(alpha = 0.5f)
                                                )
                                            )
                                        }

                                        // Weight Field
                                        Column {
                                            Text(
                                                text = "Cân nặng",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = Color.Black,
                                                modifier = Modifier.padding(bottom = 4.dp)
                                            )
                                            OutlinedTextField(
                                                value = weight,
                                                onValueChange = { },
                                                modifier = Modifier.fillMaxWidth(),
                                                enabled = false,
                                                shape = RoundedCornerShape(12.dp),
                                                colors = OutlinedTextFieldDefaults.colors(
                                                    disabledTextColor = Color.Black,
                                                    disabledBorderColor = Color.Gray.copy(alpha = 0.5f)
                                                )
                                            )
                                        }

                                        // Goal Field
                                        Column {
                                            Text(
                                                text = "Mục tiêu",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = Color.Black,
                                                modifier = Modifier.padding(bottom = 4.dp)
                                            )
                                            OutlinedTextField(
                                                value = goal,
                                                onValueChange = { },
                                                modifier = Modifier.fillMaxWidth(),
                                                enabled = false,
                                                shape = RoundedCornerShape(12.dp),
                                                colors = OutlinedTextFieldDefaults.colors(
                                                    disabledTextColor = Color.Black,
                                                    disabledBorderColor = Color.Gray.copy(alpha = 0.5f)
                                                )
                                            )
                                        }
                                    }
                                }
                            }

                            // PHẦN 2: BẢO MẬT & ĐĂNG NHẬP
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                                    .shadow(
                                        elevation = 4.dp,
                                        shape = RoundedCornerShape(16.dp)
                                    ),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Column(
                                    modifier = Modifier.padding(20.dp)
                                ) {
                                    // Section Header - Bảo mật & Đăng nhập
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 16.dp)
                                            .background(
                                                Color(0xFF3B66FF).copy(alpha = 0.1f),
                                                RoundedCornerShape(8.dp)
                                            )
                                    ) {
                                        Text(
                                            text = "2. Bảo mật & đăng nhập",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF3B66FF),
                                            modifier = Modifier
                                                .padding(vertical = 8.dp, horizontal = 16.dp)
                                                .fillMaxWidth(),
                                            textAlign = TextAlign.Center
                                        )
                                    }


                                    // Block 3: Liên kết tài khoản
                                    Column {
                                        Text(
                                            text = "Liên kết tài khoản",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp,
                                            color = Color(0xFF1A1A1A)
                                        )
                                        Text(
                                            text = "Kết nối với mạng xã hội",
                                            fontSize = 14.sp,
                                            color = Color(0xFF9E9E9E),
                                            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                                        )

                                        // Google Account
                                        val isGoogleLinked = providers.contains("google.com")
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 12.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Image(
                                                    painter = painterResource(id = R.drawable.google),
                                                    contentDescription = "Google Icon",
                                                    modifier = Modifier
                                                        .size(40.dp)
                                                        .clip(CircleShape),
                                                    contentScale = ContentScale.Crop
                                                )
                                                Spacer(modifier = Modifier.width(12.dp))
                                                Text(
                                                    text = "Google",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 16.sp,
                                                    color = Color(0xFF1A1A1A)
                                                )
                                            }

                                            Text(
                                                text = if (isGoogleLinked) "Đã liên kết" else "Chưa liên kết",
                                                fontSize = 14.sp,
                                                color = if (isGoogleLinked) Color(0xFF4CAF50) else Color(0xFF9E9E9E),
                                                fontWeight = FontWeight.Medium
                                            )
                                        }

                                        // Divider between accounts
                                        Divider(
                                            color = Color(0xFFE0E0E0).copy(alpha = 0.5f),
                                            thickness = 1.dp
                                        )

                                        // Github Account
                                        val isGithubLinked = providers.contains("github.com")
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 12.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Image(
                                                    painter = painterResource(id = R.drawable.github),
                                                    contentDescription = "Github Icon",
                                                    modifier = Modifier
                                                        .size(40.dp)
                                                        .clip(CircleShape),
                                                    contentScale = ContentScale.Crop
                                                )
                                                Spacer(modifier = Modifier.width(12.dp))
                                                Text(
                                                    text = "Github",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 16.sp,
                                                    color = Color(0xFF1A1A1A)
                                                )
                                            }

                                            Text(
                                                text = if (isGithubLinked) "Đã liên kết" else "Chưa liên kết",
                                                fontSize = 14.sp,
                                                color = if (isGithubLinked) Color(0xFF4CAF50) else Color(0xFF9E9E9E),
                                                fontWeight = FontWeight.Medium
                                            )
                                        }

                                        // Divider between accounts
                                        Divider(
                                            color = Color(0xFFE0E0E0).copy(alpha = 0.5f),
                                            thickness = 1.dp
                                        )

                                        // Email Account
                                        val isEmailLinked = providers.contains("password")
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 12.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Image(
                                                    painter = painterResource(id = R.drawable.ic_email),
                                                    contentDescription = "Email Icon",
                                                    modifier = Modifier
                                                        .size(40.dp)
                                                        .clip(CircleShape),
                                                    contentScale = ContentScale.Crop
                                                )
                                                Spacer(modifier = Modifier.width(12.dp))
                                                Text(
                                                    text = "Email",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 16.sp,
                                                    color = Color(0xFF1A1A1A)
                                                )
                                            }

                                            Text(
                                                text = if (isEmailLinked) "Đã liên kết" else "Chưa liên kết",
                                                fontSize = 14.sp,
                                                color = if (isEmailLinked) Color(0xFF4CAF50) else Color(0xFF9E9E9E),
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }
                                }
                            }

                            // PHẦN 3: CÀI ĐẶT TÀI KHOẢN
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                                    .shadow(
                                        elevation = 4.dp,
                                        shape = RoundedCornerShape(16.dp)
                                    ),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Column(
                                    modifier = Modifier.padding(20.dp)
                                ) {
                                    // Section Header - Cài đặt tài khoản
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 16.dp)
                                            .background(
                                                Color(0xFF3B66FF).copy(alpha = 0.1f),
                                                RoundedCornerShape(8.dp)
                                            )
                                    ) {
                                        Text(
                                            text = "3. Cài đặt thông báo",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF3B66FF),
                                            modifier = Modifier
                                                .padding(vertical = 8.dp, horizontal = 16.dp)
                                                .fillMaxWidth(),
                                            textAlign = TextAlign.Center
                                        )
                                    }

                                    // Block 3: Thông báo
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text(
                                                text = "Thông báo",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 16.sp,
                                                color = Color(0xFF1A1A1A)
                                            )
                                            Text(
                                                text = "Nhận thông báo từ ứng dụng",
                                                fontSize = 14.sp,
                                                color = Color(0xFF9E9E9E),
                                                modifier = Modifier.padding(top = 4.dp)
                                            )
                                        }

                                        Switch(
                                            checked = notificationsEnabled,
                                            onCheckedChange = { handleNotificationSwitchChange(it) },
                                            colors = SwitchDefaults.colors(
                                                checkedThumbColor = Color(0xFF3B66FF),
                                                checkedTrackColor = Color(0xFF3B66FF).copy(alpha = 0.5f),
                                                uncheckedThumbColor = Color(0xFF9E9E9E),
                                                uncheckedTrackColor = Color(0xFFE0E0E0)
                                            )
                                        )
                                    }
                                }
                            }

                            // PHẦN 4: HỖ TRỢ & KHÁC
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                                    .shadow(
                                        elevation = 4.dp,
                                        shape = RoundedCornerShape(16.dp)
                                    ),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Column(
                                    modifier = Modifier.padding(20.dp)
                                ) {
                                    // Section Header - Hỗ trợ & khác
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 16.dp)
                                            .background(
                                                Color(0xFF3B66FF).copy(alpha = 0.1f),
                                                RoundedCornerShape(8.dp)
                                            )
                                    ) {
                                        Text(
                                            text = "4. Hỗ trợ & khác",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF3B66FF),
                                            modifier = Modifier
                                                .padding(vertical = 8.dp, horizontal = 16.dp)
                                                .fillMaxWidth(),
                                            textAlign = TextAlign.Center
                                        )
                                    }

                                    // Block 3: Điều khoản sử dụng
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = "Điều khoản sử dụng",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 16.sp,
                                                color = Color(0xFF1A1A1A)
                                            )
                                            Text(
                                                text = "Xem điều khoản và chính sách bảo mật",
                                                fontSize = 14.sp,
                                                color = Color(0xFF9E9E9E),
                                                modifier = Modifier.padding(top = 4.dp)
                                            )
                                        }

                                        OutlinedButton(
                                            onClick = { navController?.navigate(NavRoutes.Terms) },
                                            shape = RoundedCornerShape(10.dp),
                                            colors = ButtonDefaults.outlinedButtonColors(
                                                contentColor = Color(0xFF1A1A1A),
                                                containerColor = Color.White
                                            ),
                                            border = BorderStroke(1.dp, Color(0xFF1A1A1A)),
                                            modifier = Modifier
                                                .width(100.dp)
                                                .height(40.dp)
                                        ) {
                                            Text(
                                                text = "Xem",
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF1A1A1A)
                                            )
                                        }
                                    }
                                }
                            }

                            // PHẦN 6: ĐĂNG XUẤT
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                                    .shadow(
                                        elevation = 4.dp,
                                        shape = RoundedCornerShape(16.dp)
                                    ),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Button(
                                    onClick = {
                                        // Hủy tất cả thông báo khi đăng xuất
                                        MainApplication.cancelAllDailyReminders(context)
                                        with(sharedPreferences.edit()) {
                                            putBoolean("notifications_enabled", false)
                                            apply()
                                        }

                                        // Xóa flag profile và signOut
                                        val sharedPref = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                                        val userId = FirebaseAuth.getInstance().currentUser?.uid

                                        if (userId != null) {
                                            sharedPref.edit()
                                                .remove("has_completed_profile_$userId")
                                                .apply()
                                        }

                                        FirebaseAuth.getInstance().signOut()

                                        // Navigate về Login và xóa toàn bộ back stack
                                        navController?.navigate(NavRoutes.Login) {
                                            popUpTo(0) { inclusive = true }
                                        }
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.White
                                    ),
                                    border = BorderStroke(1.dp, Color.LightGray),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp)
                                ) {
                                    Text(
                                        text = "Đăng xuất",
                                        color = Color.Black,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(60.dp))
                        }
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSettingScreen() {
    MaterialTheme {
        SettingScreen()
    }
}
