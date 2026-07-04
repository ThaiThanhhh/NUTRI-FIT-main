package com.example.nutrifit.ui.screens.ScanScreen

import android.Manifest
import android.graphics.Bitmap
import android.graphics.Matrix
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.PermissionChecker
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.nutrifit.R
import kotlinx.coroutines.flow.MutableStateFlow

fun rotateBitmap(bitmap: Bitmap, degrees: Float): Bitmap {
    if (degrees == 0f) return bitmap
    val matrix = Matrix().apply { postRotate(degrees) }
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanScreen(navController: NavController, viewModel: FoodScanViewModel = viewModel()) {
    val context = LocalContext.current

    val capturedImage by viewModel.capturedImage.collectAsState()
    val nutritionInfo by viewModel.nutritionInfo.collectAsState()
    val detectedFoodName by viewModel.detectedFoodName.collectAsState()
    val isAnalyzing by viewModel.isAnalyzing.collectAsState()

    var userFoodInput by remember { mutableStateOf("") }

    // 🛠️ SỬA ĐỔI LOGIC THÔNG MINH: Ô nhập liệu sẽ hiện ra khi:
    // 1. Chưa quét ra món nào (null hoặc unknown)
    // 2. Quét ra nhãn chung chung (Trái cây, Món ăn chung)
    // 3. Quét ra món lạ (như Jeans, Toy...) mà trong Database của nhóm KHÔNG có thông tin dinh dưỡng (nutritionInfo == null)
    val shouldShowInputBox = remember(detectedFoodName, nutritionInfo) {
        detectedFoodName == null ||
                detectedFoodName == "unknown" ||
                detectedFoodName == "Trái cây" ||
                detectedFoodName == "Món ăn chung" ||
                nutritionInfo == null
    }

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PermissionChecker.PERMISSION_GRANTED
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
        bitmap?.let {
            val correctedBitmap = rotateBitmap(it, 90f)
            viewModel.updateImage(correctedBitmap)
            userFoodInput = ""
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black)
            .padding(WindowInsets.statusBars.asPaddingValues())
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Button(
                onClick = { navController.popBackStack() },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Quay về")
            }
        }

        item {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(text = "Kiểm Tra Dinh Dưỡng", fontSize = 20.sp, color = Color.White, fontWeight = FontWeight.Bold)
            }
        }

        item {
            Card(
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier.fillMaxWidth().height(260.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Black)
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
        }

        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .weight(1.2f)
                        .background(color = Color(0xFF2A2A2A), shape = RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        isAnalyzing -> {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CircularProgressIndicator(modifier = Modifier.size(14.dp), color = Color.Yellow, strokeWidth = 1.5.dp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "Đang phân tích...", color = Color.Yellow, fontSize = 12.sp)
                            }
                        }
                        detectedFoodName != null -> {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "Đã nhận diện:", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                                Text(
                                    text = detectedFoodName!!,
                                    color = if (nutritionInfo == null || detectedFoodName == "Trái cây" || detectedFoodName == "Món ăn chung") Color.Yellow else Color.Green,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                        else -> {
                            Text(text = "Chưa nhận diện món ăn", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp, fontStyle = FontStyle.Italic)
                        }
                    }
                }

                Button(
                    onClick = {
                        if (!hasCameraPermission) {
                            permissionLauncher.launch(Manifest.permission.CAMERA)
                        } else {
                            cameraLauncher.launch(null)
                        }
                    },
                    modifier = Modifier.weight(0.8f),
                    enabled = !isAnalyzing
                ) {
                    Text("📷 Chụp ảnh")
                }
            }
        }

        // 🚀 HIỂN THỊ HỘP NHẬP LIỆU GÕ THỦ CÔNG KHI CHƯA CHÍNH XÁC HOẶC KHÔNG CÓ TRONG DB
        if (capturedImage != null && shouldShowInputBox && !isAnalyzing) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E251F)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "💡 Nếu chúng tôi không thể đưa ra kết quả chính xác, bạn có thể nhập tên thông tin đồ ăn vào đây:",
                            color = Color(0xFFE0E0E0),
                            fontSize = 12.sp,
                            lineHeight = 16.sp,
                            fontStyle = FontStyle.Italic
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            TextField(
                                value = userFoodInput,
                                onValueChange = { userFoodInput = it },
                                placeholder = { Text("Ví dụ: táo, phở gà, bún chả...", fontSize = 13.sp, color = Color.Gray) },
                                modifier = Modifier.weight(1f).height(50.dp),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color(0xFF2A2A2A),
                                    unfocusedContainerColor = Color(0xFF2A2A2A),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedIndicatorColor = Color.Green,
                                    unfocusedIndicatorColor = Color.Transparent
                                ),
                                shape = RoundedCornerShape(8.dp),
                                singleLine = true
                            )
                            Button(
                                onClick = {
                                    if (userFoodInput.trim().isNotEmpty()) {
                                        val cleanInput = userFoodInput.trim().lowercase()
                                        val mappedFood = NutritionRepository.findSimilarFood(cleanInput) ?: cleanInput
                                        val exactDbName = NutritionRepository.getAllFoodNames().firstOrNull {
                                            it == mappedFood || it.contains(mappedFood) || mappedFood.contains(it)
                                        }

                                        if (exactDbName != null) {
                                            Toast.makeText(context, "Đã cập nhật dữ liệu: $exactDbName", Toast.LENGTH_SHORT).show()
                                            try {
                                                val methodDetected = viewModel.javaClass.getDeclaredField("_detectedFoodName").apply { isAccessible = true }
                                                val methodNutrition = viewModel.javaClass.getDeclaredField("_nutritionInfo").apply { isAccessible = true }
                                                val methodCalories = viewModel.javaClass.getDeclaredField("_estimatedCalories").apply { isAccessible = true }

                                                (methodDetected.get(viewModel) as MutableStateFlow<String?>).value = exactDbName
                                                (methodNutrition.get(viewModel) as MutableStateFlow<NutritionInfo?>).value = NutritionRepository.getNutritionInfo(exactDbName)
                                                (methodCalories.get(viewModel) as MutableStateFlow<Int?>).value = NutritionRepository.getCaloriesForLabel(exactDbName)
                                            } catch(e: Exception) {
                                                e.printStackTrace()
                                            }
                                        } else {
                                            Toast.makeText(context, "Không tìm thấy món '$userFoodInput' trong danh mục!", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(50.dp)
                            ) {
                                Text("Tra cứu", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Bảng giá trị dinh dưỡng
        item {
            Card(
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2A2A))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Các chất dinh dưỡng trong món",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    if (nutritionInfo != null && detectedFoodName != null && detectedFoodName != "unknown") {
                        NutritionItem("Trọng lượng:", nutritionInfo!!.weight)
                        NutritionItem("Năng lượng:", "${nutritionInfo!!.calories} kcal")
                        NutritionItem("Protein:", nutritionInfo!!.protein)
                        NutritionItem("Carbohydrate:", nutritionInfo!!.carbs)
                        NutritionItem("Chất béo:", nutritionInfo!!.fat)
                        NutritionItem("Chất xơ:", nutritionInfo!!.fiber)
                    } else {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "Chưa có thông tin dinh dưỡng chính xác.", color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp, textAlign = TextAlign.Center)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Vui lòng chụp cận cảnh món ăn hoặc nhập tên món ăn vào ô tra cứu thủ công ở trên.",
                                color = Color.Yellow.copy(alpha = 0.8f), fontSize = 12.sp, fontStyle = FontStyle.Italic, textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NutritionItem(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
        Text(text = value, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}