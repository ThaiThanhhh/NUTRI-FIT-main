package com.example.nutrifit.ui.screens.meal

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.nutrifit.R
import com.example.nutrifit.data.model.Meal
import com.example.nutrifit.viewmodel.AddMealState
import com.example.nutrifit.viewmodel.MealDetailState
import com.example.nutrifit.viewmodel.MealViewModel
import androidx.compose.foundation.BorderStroke
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealDetailScreen(mealId: Int, navController: NavController) {
    val mealViewModel: MealViewModel = viewModel()
    val context = LocalContext.current

    // Fetch the meal details when the screen is composed
    LaunchedEffect(key1 = mealId) {
        mealViewModel.fetchMealById(mealId)
    }

    val mealDetailState by mealViewModel.mealDetailState.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        when (val state = mealDetailState) {
            is MealDetailState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            is MealDetailState.Error -> {
                Text(state.message, modifier = Modifier.align(Alignment.Center), color = Color.Red)
            }
            is MealDetailState.Success -> {
                val meal = state.meal
                MealDetailContent(meal = meal, navController = navController, viewModel = mealViewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealDetailContent(meal: Meal, navController: NavController, viewModel: MealViewModel) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    val addMealState by viewModel.addMealState.collectAsState()
    LaunchedEffect(addMealState) {
        when (addMealState) {
            is AddMealState.Success -> {
                Toast.makeText(context, "Đã thêm '${meal.name}' vào bữa ăn!", Toast.LENGTH_SHORT).show()
                viewModel.resetAddMealState() // Reset state to avoid showing toast again
            }
            is AddMealState.Error -> {
                Toast.makeText(context, "Lỗi: ${(addMealState as AddMealState.Error).message}", Toast.LENGTH_SHORT).show()
                viewModel.resetAddMealState()
            }
            else -> Unit
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(scrollState)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
        ) {
            val imageResId = context.resources.getIdentifier(meal.imageRes, "drawable", context.packageName)
            Image(
                painter = painterResource(id = if (imageResId != 0) imageResId else R.drawable.logo),
                contentDescription = meal.name,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp)),
                contentScale = ContentScale.Crop
            )

            Card(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .padding(start = 16.dp, top = 32.dp, end = 16.dp) // Adjusted padding
                    .width(100.dp)
                    .height(36.dp),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.6f))
            ) {
                Row(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Quay về", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = meal.name,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A),
                fontSize = 26.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Box(modifier = Modifier.width(220.dp).height(3.dp).background(Color(0xFFC9B5F8)))
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            NutritionCard(
                title = "${meal.calories} kcal",
                subtitle = "Năng lượng",
                color = Color(0xFFFFE9CC),
                icon = Icons.Default.Whatshot,
                iconColor = Color(0xFFFF6B35),
                modifier = Modifier.weight(1.5f)
            )
            NutritionCard(
                title = meal.time,
                subtitle = "Thời gian",
                color = Color(0xFFF2E7FB),
                icon = Icons.Default.Schedule,
                iconColor = Color(0xFF9C27B0),
                modifier = Modifier.weight(1.3f)
            )
            NutritionCard(
                title = meal.difficulty,
                subtitle = "Mức độ",
                color = Color(0xFFE9F8E6),
                icon = Icons.Default.Check,
                iconColor = Color(0xFF4CAF50),
                modifier = Modifier.weight(1.3f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        val totalGrams = meal.protein + meal.carbs + meal.fat
        val proteinPercentage = if (totalGrams > 0) (meal.protein * 100) / totalGrams else 0
        val carbsPercentage = if (totalGrams > 0) (meal.carbs * 100) / totalGrams else 0
        val fatPercentage = if (totalGrams > 0) (meal.fat * 100) / totalGrams else 0

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            NutritionRingCard(percentage = proteinPercentage, title = "Protein", grams = "${meal.protein}g", ringColor = Color(0xFFFFA25C), backgroundColor = Color(0xFFF9D8E0), modifier = Modifier.weight(1f))
            NutritionRingCard(percentage = carbsPercentage, title = "Tinh bột", grams = "${meal.carbs}g", ringColor = Color(0xFFA5E3A4), backgroundColor = Color(0xFFE4F9E3), modifier = Modifier.weight(1f))
            NutritionRingCard(percentage = fatPercentage, title = "Chất béo", grams = "${meal.fat}g", ringColor = Color(0xFFFFA25C), backgroundColor = Color(0xFFFFE8D1), modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(32.dp))
        DescriptionSection(description = meal.description)
        Spacer(modifier = Modifier.height(24.dp))
        CookingInstructionsSection(meal.instructions)
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { viewModel.addMealToIntake(meal, "Bữa ăn") },
            modifier = Modifier.fillMaxWidth().height(48.dp).padding(horizontal = 20.dp).shadow(elevation = 4.dp, shape = RoundedCornerShape(10.dp), clip = false),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
        ) {
            Text("Thêm vào bữa ăn hôm nay", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth().height(48.dp).padding(horizontal = 20.dp),
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(1.dp, Color.Gray)
        ) {
            Text("Xem món ăn khác", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.Gray)
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun NutritionCard(
    title: String,
    subtitle: String,
    color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(80.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = color),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(imageVector = icon, contentDescription = subtitle, tint = iconColor, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Column(horizontalAlignment = Alignment.Start) {
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = Color.Black.copy(alpha = 0.8f), fontSize = 12.sp)
                Spacer(modifier = Modifier.height(2.dp))
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun NutritionRingCard(
    percentage: Int,
    title: String,
    grams: String,
    ringColor: Color,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(130.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp, vertical = 14.dp)
        ) {
            Box(modifier = Modifier.size(70.dp).align(Alignment.TopCenter)) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidth = 6.dp.toPx()
                    val radius = (size.minDimension - strokeWidth) / 2
                    val center = Offset(size.width / 2, size.height / 2)
                    drawCircle(color = backgroundColor, radius = radius, center = center, style = Stroke(strokeWidth))
                    val sweepAngle = (percentage * 360f) / 100f
                    drawArc(color = ringColor, startAngle = -90f, sweepAngle = sweepAngle, useCenter = false, topLeft = Offset(center.x - radius, center.y - radius), size = Size(radius * 2, radius * 2), style = Stroke(strokeWidth))
                }
                Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("$percentage%", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A1A), fontSize = 14.sp)
                    Text(grams, style = MaterialTheme.typography.bodySmall, color = Color(0xFF666666), fontSize = 10.sp)
                }
            }
            Text(title, style = MaterialTheme.typography.bodyMedium, color = Color(0xFF333333), fontWeight = FontWeight.Medium, fontSize = 12.sp, modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 8.dp))
        }
    }
}

@Composable
fun DescriptionSection(description: String) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(2.dp, Color(0xFFF8D766).copy(alpha = 0.6f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Mô tả phần ăn", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.Black, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(12.dp))
                Box(modifier = Modifier.width(140.dp).height(3.dp).offset(y = (-12).dp).background(Color(0xFFC9B5F8)))
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(description, style = MaterialTheme.typography.bodyMedium, color = Color(0xFF666666), lineHeight = 22.sp)
        }
    }
}

@Composable
fun CookingInstructionsSection(instruction: String) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(2.dp, Color(0xFF5877A4))

    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Cách chế biến", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 22.sp, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(12.dp))
                Box(modifier = Modifier.width(140.dp).height(3.dp).offset(y = (-12).dp).background(Color(0xFFC9B5F8)))
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(instruction, style = MaterialTheme.typography.bodyMedium, color = Color(0xFF333333), lineHeight = 20.sp, fontSize = 14.sp)
        }
    }
}

@Composable
fun NumberedInstruction(number: Int, title: String, description: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text("$number.", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Color(0xFF1C3C64), fontSize = 16.sp, modifier = Modifier.padding(top = 2.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = Color(0xFF1C3C64), fontSize = 16.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(description, style = MaterialTheme.typography.bodyMedium, color = Color(0xFF333333), lineHeight = 20.sp, fontSize = 14.sp)
        }
    }
}

@Preview(showBackground = true, device = "spec:width=411dp,height=877dp")
@Composable
fun MealDetailScreenPreview() {
    MaterialTheme {
        val navController = rememberNavController()
        val previewMeal = Meal(
            id = 1, 
            name = "Ức gà và Khoai lang", 
            description = "Bữa ăn ngon và bổ dưỡng cho người tập luyện", 
            imageRes = "uc_ga_bong_cai_xanh",
            calories = 350, 
            time = "20-25 phút", 
            category = "Bữa chính",
            protein = 40,
            carbs = 30,
            fat = 10,
            difficulty = "Dễ",
            instructions = "1. Sơ chế nguyên liệu...\n2. Nấu khoai lang...\n3. Chế biến ức gà...",
            suitableGoals = listOf("Tăng cơ", "Giảm mỡ")
        )
        MealDetailContent(meal = previewMeal, navController = navController, viewModel = viewModel())
    }
}
