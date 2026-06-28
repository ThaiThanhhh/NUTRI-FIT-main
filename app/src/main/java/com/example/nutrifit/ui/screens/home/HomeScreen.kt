package com.example.nutrifit.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import coil.compose.rememberImagePainter
import com.example.nutrifit.R
import com.example.nutrifit.data.model.DailyIntake
import com.example.nutrifit.data.model.Exercise
import com.example.nutrifit.ui.navigation.NavRoutes
import com.example.nutrifit.ui.screens.meal.MealCard
import com.example.nutrifit.viewmodel.DailyIntakeState
import com.example.nutrifit.viewmodel.HomeViewModel
import com.example.nutrifit.viewmodel.MealsState
import com.example.nutrifit.viewmodel.SuggestedExercisesState
import com.example.nutrifit.viewmodel.UserState
import java.util.Calendar

// Data class for weekly chart
data class WeeklyData(
    val day: String,
    val caloriesConsumed: Float,
    val caloriesBurned: Float
)

@Composable
fun HomeScreen(navController: NavController) {
    val viewModel: HomeViewModel = viewModel()
    val userState by viewModel.userState.collectAsState()
    val dailyIntakeState by viewModel.dailyIntakeState.collectAsState()
    val weeklyIntakeState by viewModel.weeklyIntakeState.collectAsState()
    val suggestedMealsState by viewModel.suggestedMealsState.collectAsState()
    val suggestedExercisesState by viewModel.suggestedExercisesState.collectAsState()
    val context = LocalContext.current


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFFFAFAFA)
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            item {
                // Header Section
                Box() {
                    Image(
                        painter = painterResource(id = R.drawable.home1),
                        contentDescription = "Header background",
                        modifier = Modifier.fillMaxWidth(),
                        contentScale = ContentScale.Crop
                    )
                    Row(verticalAlignment = Alignment.CenterVertically){
                        Text(
                            text = buildAnnotatedString {
                                withStyle(style = SpanStyle(color = Color(0xFF1AC9AC), fontWeight = FontWeight.Bold)) { append("NUTRI") }
                                withStyle(style = SpanStyle(color = colorScheme.onBackground, fontWeight = FontWeight.Bold)) { append(" - ") }
                                withStyle(style = SpanStyle(color = Color.Red, fontWeight = FontWeight.Bold)) { append("FIT") }
                            },
                            style = MaterialTheme.typography.headlineSmall,
                            fontSize = 37.sp,
                            modifier = Modifier.padding(top = 16.dp, start = 30.dp)
                        )
                        Spacer(modifier = Modifier.weight(1f))

                        Box(modifier = Modifier.padding(top = 10.dp, end = 30.dp)) {
                            Image(
                                painter = painterResource(id = R.drawable.bgavt),
                                contentDescription = "Avatar background",
                                modifier = Modifier.size(63.dp)
                            )
                            Image(
                                painter = painterResource(id = R.drawable.avt),
                                contentDescription = "Avatar",
                                modifier = Modifier.size(63.dp)
                            )
                        }
                    }
                }
            }
            item{
                Spacer(modifier = Modifier.height(25.dp))
                Column(modifier = Modifier.fillMaxWidth()) {
                    val greeting = when (val state = userState) {
                        is UserState.Success -> "Chào ${state.user.name}!"
                        else -> "Chào bạn!"
                    }
                    Text(
                        text = greeting,
                        color = Color.Black,
                        fontSize = 20.sp,
                        modifier = Modifier
                            .align(Alignment.Start)
                            .padding(start = 25.dp)
                    )
                }
            }

            // Goal and Progress Section
            item{
                Spacer(modifier = Modifier.height(20.dp))
                Column(
                    modifier = Modifier.fillMaxSize().padding(start = 17.dp, end = 17.dp)
                ){
                    Box() {
                        Image(
                            painter = painterResource(id = R.drawable.khungmuctieu),
                            contentDescription = "khung muc tieu",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(450.dp), // Adjusted height for legend
                            contentScale = ContentScale.Crop
                        )
                        Column(modifier = Modifier.fillMaxWidth().padding(top = 20.dp, start = 20.dp, end = 20.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.bieutuonglop3),
                                    contentDescription = "Goal icon",
                                    modifier = Modifier.size(60.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    val userGoalText = when (val state = userState) {
                                        is UserState.Success -> state.user.goal ?: "Chưa có mục tiêu"
                                        else -> "Chưa có mục tiêu"
                                    }
                                    Text(
                                        text = "Mục tiêu: $userGoalText",
                                        fontSize = 13.sp,
                                        color = Color.Black,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 2
                                    )
                                    Spacer(modifier = Modifier.height(7.dp))

                                    val calorieGoalText = when (val state = userState) {
                                        is UserState.Success -> state.user.calorieGoal?.toString() ?: "..."
                                        else -> "..."
                                    }

                                    Text(
                                        text = "Mục tiêu: $calorieGoalText calo/ngày",
                                        fontSize = 11.sp,
                                        color = Color.Gray,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            val dailyIntake = (dailyIntakeState as? DailyIntakeState.Success)?.intake
                            val user = (userState as? UserState.Success)?.user

                            val caloriesConsumed = dailyIntake?.getTotalCaloriesConsumed() ?: 0
                            val caloriesBurned = dailyIntake?.getTotalCaloriesBurned() ?: 0
                            val netCalories = dailyIntake?.getNetCalories() ?: 0
                            val calorieGoal = user?.calorieGoal ?: 2000

                            val progress = if (calorieGoal > 0) (caloriesConsumed.toFloat() / calorieGoal.toFloat()).coerceIn(0f, 1f) else 0f
                            val progressPercentage = (progress * 100).toInt()

                            Spacer(modifier = Modifier.height(20.dp))
                            Box(modifier = Modifier.fillMaxWidth().height(15.dp).background(Color(0xFFD9D9D9), shape = RoundedCornerShape(50))) {
                                Box(modifier = Modifier.fillMaxWidth(progress).fillMaxHeight().background(Color(0xFF4CAF50), shape = RoundedCornerShape(50)))
                            }

                            Spacer(modifier = Modifier.height(20.dp))
                            Column(Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Hấp thụ: $caloriesConsumed cal", fontSize = 13.sp, color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold)
                                    Text("$progressPercentage%", fontSize = 13.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Luyện tập: $caloriesBurned cal", fontSize = 13.sp, color = Color.Red, fontWeight = FontWeight.Bold)
                                    Text("Tổng: $netCalories cal", fontSize = 13.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier .height(20.dp))
                            Text("Tiến trình hàng tuần", fontSize = 17.sp, color = Color.Black)

                            // Weekly progress chart
                            Spacer(modifier = Modifier.height(6.dp))
                            Box(modifier = Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(20.dp)).padding(10.dp)) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    val daysInWeek = listOf("T2", "T3", "T4", "T5", "T6", "T7", "CN")
                                    val weeklyData = daysInWeek.map { dayString ->
                                        val calendar = Calendar.getInstance()
                                        calendar.firstDayOfWeek = Calendar.MONDAY
                                        val dayOfWeek = when (dayString) {
                                            "T2" -> Calendar.MONDAY
                                            "T3" -> Calendar.TUESDAY
                                            "T4" -> Calendar.WEDNESDAY
                                            "T5" -> Calendar.THURSDAY
                                            "T6" -> Calendar.FRIDAY
                                            "T7" -> Calendar.SATURDAY
                                            "CN" -> Calendar.SUNDAY
                                            else -> -1
                                        }
                                        val intakeForDay = weeklyIntakeState.find { intake ->
                                            calendar.time = intake.date
                                            calendar.get(Calendar.DAY_OF_WEEK) == dayOfWeek
                                        }
                                        WeeklyData(
                                            day = dayString,
                                            caloriesConsumed = intakeForDay?.getTotalCaloriesConsumed()?.toFloat() ?: 0f,
                                            caloriesBurned = intakeForDay?.getTotalCaloriesBurned()?.toFloat() ?: 0f
                                        )
                                    }
                                    WeeklyProgressChart(weeklyData = weeklyData, calorieGoal = calorieGoal)

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Legend
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Box(modifier = Modifier.size(12.dp).background(Color(0xFF4CAF50), RoundedCornerShape(2.dp)))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Hấp thụ", fontSize = 12.sp, color = Color.Black)
                                        Spacer(modifier = Modifier.width(16.dp))
                                        Box(modifier = Modifier.size(12.dp).background(Color.Red, RoundedCornerShape(2.dp)))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Luyện tập", fontSize = 12.sp, color = Color.Black)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Action buttons section
            item {
                Spacer(modifier = Modifier.height(7.dp))
                Column(modifier = Modifier.padding(start = 15.dp, end = 15.dp) .fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally){
                    Box {
                        Image(painter = painterResource(id = R.drawable.khung4nut), contentDescription = "", modifier = Modifier.fillMaxWidth(), contentScale = ContentScale.Crop)
                        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 5.dp), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
                            ActionButton(navController = navController, route = NavRoutes.Meal, iconRes = R.drawable.thucdon, label = "Thực đơn", modifier = Modifier.weight(1f))
                            ActionButton(navController = navController, route = NavRoutes.Workout, iconRes = R.drawable.baitap, label = "Bài tập", modifier = Modifier.weight(1f))
                            ActionButton(navController = navController, route = NavRoutes.Scan, iconRes = R.drawable.kiemtra, label = "Kiểm tra", modifier = Modifier.weight(1f))
                            ActionButton(navController = navController, route = NavRoutes.Schedule, iconRes = R.drawable.lichtap, label = "Lịch tập", modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            // Meal Suggestions Section
            item{
                Spacer(modifier = Modifier.height(20.dp))
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Đề xuất bữa ăn", color = Color.Black, fontSize = 20.sp, modifier = Modifier.align(Alignment.Start).padding(start = 25.dp))
                }
            }

            item {
                when(val state = suggestedMealsState) {
                    is MealsState.Loading -> {
                        CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                    }
                    is MealsState.Error -> {
                        Text(state.message, modifier = Modifier.padding(16.dp), color = Color.Red)
                    }
                    is MealsState.Success -> {
                        if (state.meals.isEmpty()) {
                            Text("Không có đề xuất nào phù hợp.", modifier = Modifier.padding(16.dp))
                        } else {
                            Row(
                                modifier = Modifier.horizontalScroll(rememberScrollState())
                                    .padding(horizontal = 16.dp)
                            ) {
                                state.meals.forEach { meal ->
                                    MealCard(meal = meal) {
                                        navController.navigate("${NavRoutes.MealDetail}/${meal.id}")
                                    }
                                    Spacer(modifier = Modifier.width(16.dp))
                                }
                            }
                        }
                    }
                }
            }

            // Exercise Suggestions Section
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Đề xuất bài tập", color = Color.Black, fontSize = 20.sp, modifier = Modifier.align(Alignment.Start).padding(start = 25.dp))
                }
            }

            item {
                when (val state = suggestedExercisesState) {
                    is SuggestedExercisesState.Loading -> {
                        CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                    }
                    is SuggestedExercisesState.Error -> {
                        Text(state.message, modifier = Modifier.padding(16.dp), color = Color.Red)
                    }
                    is SuggestedExercisesState.Success -> {
                        if (state.exercises.isEmpty()) {
                            Text("Không có đề xuất nào phù hợp.", modifier = Modifier.padding(16.dp))
                        } else {
                            Row(
                                modifier = Modifier.horizontalScroll(rememberScrollState())
                                    .padding(horizontal = 16.dp)
                            ) {
                                state.exercises.forEach { exercise ->
                                    ExerciseCard(exercise = exercise) {
                                        navController.navigate("${NavRoutes.WORKOUT_DETAIL}/${exercise.name}")
                                    }
                                    Spacer(modifier = Modifier.width(16.dp))
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

@Composable
fun ActionButton(navController: NavController, route: String, iconRes: Int, label: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .padding(6.dp)
            .clickable {
                navController.navigate(route) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Image(painter = painterResource(id = R.drawable.khungnho4nut), contentDescription = "", modifier = Modifier.fillMaxWidth(), contentScale = ContentScale.Crop)
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(painter = painterResource(id = iconRes), contentDescription = label, modifier = Modifier.size(45.dp))
            Spacer(modifier = Modifier.height(6.dp))
            Text(label, fontSize = 11.sp, color = Color.Black)
        }
    }
}

@Composable
fun WeeklyProgressChart(weeklyData: List<WeeklyData>, calorieGoal: Int) {
    val maxCalories = calorieGoal.toFloat().takeIf { it > 0 } ?: 2000f

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .padding(top = 10.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.Bottom
    ) {
        weeklyData.forEach { data ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .height(120.dp)
                        .width(30.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    // Full-height gray background
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFFE0E0E0), shape = RoundedCornerShape(4.dp))
                    )

                    val totalCalories = data.caloriesConsumed + data.caloriesBurned
                    if (totalCalories > 0f) {
                        val totalHeight = ((totalCalories / maxCalories) * 120f).coerceAtMost(120f).dp
                        val consumedPortion = if (totalCalories > 0) data.caloriesConsumed / totalCalories else 0f

                        val consumedHeight = totalHeight * consumedPortion
                        val burnedHeight = totalHeight * (1f - consumedPortion)

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Bottom,
                            modifier = Modifier.fillMaxHeight()
                        ) {
                            // Consumed (Green)
                            if (consumedHeight > 0.dp) {
                                Box(
                                    modifier = Modifier
                                        .width(30.dp)
                                        .height(consumedHeight)
                                        .background(
                                            color = Color(0xFF4CAF50),
                                            shape = if (burnedHeight <= 0.dp) RoundedCornerShape(4.dp) else RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
                                        )
                                )
                            }
                            // Burned (Red)
                            if (burnedHeight > 0.dp) {
                                Box(
                                    modifier = Modifier
                                        .width(30.dp)
                                        .height(burnedHeight)
                                        .background(
                                            color = Color.Red,
                                            shape = if (consumedHeight <= 0.dp) RoundedCornerShape(4.dp) else RoundedCornerShape(bottomStart = 4.dp, bottomEnd = 4.dp)
                                        )
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = data.day,
                    fontSize = 12.sp,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun ExerciseCard(exercise: Exercise, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(200.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = 4.dp
    ) {
        Column {
            Image(
                painter = rememberImagePainter(data = exercise.imageUrl),
                contentDescription = exercise.name,
                modifier = Modifier
                    .height(120.dp)
                    .fillMaxWidth(),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(12.dp)) {
                Text(text = exercise.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Nhóm cơ: ${exercise.muscleGroup}", fontSize = 14.sp, color = Color.Black)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Độ khó: ${exercise.difficulty}", fontSize = 14.sp, color = Color.Black)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Reps: ${exercise.reps}", fontSize = 14.sp, color = Color.Black)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "${exercise.caloriesBurned} kcal", fontSize = 14.sp, color = Color.Red, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
