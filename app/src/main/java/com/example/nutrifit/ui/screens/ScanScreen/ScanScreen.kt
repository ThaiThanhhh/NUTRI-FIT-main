package com.example.nutrifit.ui.screens.ScanScreen

import android.Manifest
import android.graphics.Bitmap
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.nutrifit.R

@Composable
fun ScanScreen(navController: NavController, viewModel: FoodScanViewModel = viewModel()) {
    val context = LocalContext.current

    val capturedImage by viewModel.capturedImage.collectAsState()
    val labels by viewModel.labels.collectAsState()
    val estimatedCalories by viewModel.estimatedCalories.collectAsState()
    val nutritionInfo by viewModel.nutritionInfo.collectAsState()
    val detectedFoodName by viewModel.detectedFoodName.collectAsState()
    val isAnalyzing by viewModel.isAnalyzing.collectAsState()

    // permission state
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PermissionChecker.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
        if (!granted) {
            Toast.makeText(context, "Cần quyền camera để chụp ảnh", Toast.LENGTH_SHORT).show()
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        bitmap?.let { viewModel.updateImage(it) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black)
            .padding(WindowInsets.statusBars.asPaddingValues())
            .padding(start = 16.dp, end =16.dp),
    ) {
        Button(
            onClick = {navController.popBackStack() },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color.Black
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Quay về"
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Kiểm Tra Dinh Dưỡng",
                fontSize = 20.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(12.dp))

        // Hiển thị ảnh
        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Black
            )
        ) {
            if (capturedImage != null) {
                Image(
                    bitmap = capturedImage!!.asImageBitmap(),
                    contentDescription = "Captured image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.scan),
                    contentDescription = "Placeholder image",
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Hàng chứa nút chụp ảnh và tên món ăn
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(
                        color = Color(0xFF2A2A2A),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                when {
                    isAnalyzing -> {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(14.dp),
                                color = Color.Yellow,
                                strokeWidth = 1.5.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Đang phân tích...",
                                color = Color.Yellow,
                                fontSize = 12.sp
                            )
                        }
                    }
                    detectedFoodName != null -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Đã nhận diện:",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 12.sp
                            )
                            Text(
                                text = detectedFoodName!! ,
                                color = Color.Green,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            // Hiển thị thêm confidence nếu có

                        }
                    }
                    labels.isNotEmpty() -> {
                        Text(
                            text = "Tìm thấy ${labels.size} đối tượng",
                            color = Color.Yellow.copy(alpha = 0.8f),
                            fontSize = 12.sp,
                            fontStyle = FontStyle.Italic
                        )
                    }
                    else -> {
                        Text(
                            text = "Chưa nhận diện món ăn",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 12.sp,
                            fontStyle = FontStyle.Italic
                        )
                    }
                }
            }
            // Nút chụp ảnh
            Button(
                onClick = {
                    if (!hasCameraPermission) {
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    } else {
                        cameraLauncher.launch(null)
                    }
                },
                modifier = Modifier.weight(1f),
                enabled = !isAnalyzing
            ) {
                if (isAnalyzing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Đang phân tích...")
                } else {
                    Text("📷 Chụp ảnh")
                }
            }

        }

        Spacer(modifier = Modifier.height(16.dp))

        // Hiển thị thông tin dinh dưỡng chi tiết
        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF2A2A2A)
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Các chất dinh dưỡng trong món",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                if (nutritionInfo != null) {
                    // Hiển thị thông tin dinh dưỡng chi tiết
                    NutritionItem("Trọng lượng:", nutritionInfo!!.weight)
                    NutritionItem("Năng lượng:", "${nutritionInfo!!.calories} kcal")
                    NutritionItem("Protein:", nutritionInfo!!.protein)
                    NutritionItem("Carbohydrate:", nutritionInfo!!.carbs)
                    NutritionItem("Chất béo:", nutritionInfo!!.fat)
                    NutritionItem("Chất xơ:", nutritionInfo!!.fiber)
                } else if (detectedFoodName != null) {
                    Text(
                        text = "Không tìm thấy dữ liệu dinh dưỡng cho '$detectedFoodName'",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 14.sp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                } else if (labels.isNotEmpty()) {
                    Text(
                        text = "Không tìm thấy dữ liệu dinh dưỡng phù hợp",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 14.sp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                } else {
                    Text(
                        text = "Chụp ảnh món ăn để phân tích dinh dưỡng",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 14.sp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    Text(
                        text = "Lưu ý, việc phân tích thông qua hình ảnh có thể sai sót!",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 12.sp,
                        fontStyle = FontStyle.Italic
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

    }
}

@Composable
fun NutritionItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 14.sp
        )
        Text(
            text = value,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}