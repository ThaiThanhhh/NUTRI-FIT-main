package com.example.nutrifit.ui.screens.dailylog

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowLeft
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.nutrifit.R
import com.example.nutrifit.data.model.ConsumedMeal
import com.example.nutrifit.data.model.ConsumedWorkout
import com.example.nutrifit.viewmodel.DailyLogViewModel
import com.example.nutrifit.viewmodel.LogState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun DailyLogScreen(navController: NavController) {
    val viewModel: DailyLogViewModel = viewModel()
    val logState by viewModel.logState.collectAsState()
    val weekDisplay by viewModel.weekDisplay.collectAsState()
    val showDeleteConfirmationDialog = remember { mutableStateOf(false) }
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Bữa ăn", "Bài tập")

    if (showDeleteConfirmationDialog.value) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmationDialog.value = false },
            title = { Text("Xác nhận xóa") },
            text = { Text("Bạn có chắc chắn muốn xóa tất cả các mục trong tuần được hiển thị không?") },
            confirmButton = {
                Button(
                    onClick = {
                        if (selectedTabIndex == 0) viewModel.clearAllMeals()
                        else viewModel.clearAllWorkouts()
                        showDeleteConfirmationDialog.value = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) { Text("Xóa tất cả") }
            },
            dismissButton = { Button(onClick = { showDeleteConfirmationDialog.value = false }) { Text("Hủy") } }
        )
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp) // Reduced height
                    .background(MaterialTheme.colorScheme.primary),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = MaterialTheme.colorScheme.onPrimary)
                }
                Text(
                    text = "Nhật ký tuần",
                    modifier = Modifier.weight(1f).padding(start = 16.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium
                )
                val showButton = when (val state = logState) {
                    is LogState.Success -> state.meals.isNotEmpty() || state.workouts.isNotEmpty()
                    else -> false
                }
                if (showButton) {
                    IconButton(onClick = { showDeleteConfirmationDialog.value = true }) {
                        Icon(Icons.Default.Delete, "Delete All", tint = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).background(Color(0xFFF8F8F8))
        ) {
            WeekNavigationBar(weekDisplay, onPrevious = { viewModel.previousWeek() }, onNext = { viewModel.nextWeek() })
            TabRow(selectedTabIndex = selectedTabIndex, containerColor = MaterialTheme.colorScheme.primaryContainer) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title, color = if (selectedTabIndex == index) MaterialTheme.colorScheme.onPrimaryContainer else Color.White.copy(alpha = 0.7f)) }
                    )
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                when (val state = logState) {
                    is LogState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    is LogState.Success -> {
                        if (selectedTabIndex == 0) { // Meals
                            if (state.meals.isEmpty()) {
                                EmptyLogView("bữa ăn")
                            } else {
                                GroupedLazyColumn(items = state.meals, keyExtractor = { it.consumedAt }) { meal ->
                                    MealLogItem(meal = meal) { viewModel.deleteMeal(meal) }
                                }
                            }
                        } else { // Workouts
                            if (state.workouts.isEmpty()) {
                                EmptyLogView("bài tập")
                            } else {
                                GroupedLazyColumn(items = state.workouts, keyExtractor = { it.timestamp }) { workout ->
                                    WorkoutLogItem(workout = workout) { viewModel.deleteWorkout(workout) }
                                }
                            }
                        }
                    }
                    is LogState.Error -> Text(state.message, modifier = Modifier.align(Alignment.Center), color = Color.Red)
                }
            }
        }
    }
}

@Composable
fun WeekNavigationBar(weekDisplay: String, onPrevious: () -> Unit, onNext: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = onPrevious) { Icon(Icons.AutoMirrored.Filled.ArrowLeft, contentDescription = "Tuần trước") }
        Text(text = weekDisplay, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.Black)
        IconButton(onClick = onNext) { Icon(Icons.AutoMirrored.Filled.ArrowRight, contentDescription = "Tuần sau") }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T> GroupedLazyColumn(
    items: List<T>,
    keyExtractor: (T) -> Date,
    itemContent: @Composable (T) -> Unit
) {
    val grouped = items.groupBy { 
        val sdf = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.forLanguageTag("vi"))
        sdf.format(keyExtractor(it))
    }
    val dayFormatter = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.forLanguageTag("vi"))

    LazyColumn(contentPadding = PaddingValues(vertical = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        grouped.forEach { (date, itemsOnDate) ->
            stickyHeader {
                DateHeader(dateText = date)
            }
            items(itemsOnDate) { item ->
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    itemContent(item)
                    Divider(color = Color.LightGray, thickness = 0.5.dp)
                }
            }
        }
    }
}

@Composable
fun DateHeader(dateText: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF8F8F8))
            .padding(vertical = 8.dp, horizontal = 16.dp)
    ) {
        Text(
            text = dateText,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = Color.DarkGray
        )
    }
}

@Composable
fun EmptyLogView(logType: String) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Nhật ký trống", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color.Black)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Chưa có $logType nào được ghi lại trong tuần này.", fontSize = 16.sp, color = Color.Gray, textAlign = TextAlign.Center)
    }
}

@Composable
fun MealLogItem(meal: ConsumedMeal, onDelete: () -> Unit) {
    val context = LocalContext.current
    val timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())
    val time = timeFormatter.format(meal.consumedAt)

    val imageResId = remember(meal.imageRes) {
        val resId = context.resources.getIdentifier(meal.imageRes, "drawable", context.packageName)
        if (resId == 0) R.drawable.logo else resId // Fallback to a default logo
    }

    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = imageResId),
            contentDescription = meal.name,
            modifier = Modifier.size(48.dp).clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(meal.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
            Text("Lúc: $time - ${meal.mealType}", fontSize = 13.sp, color = Color.DarkGray)
        }
        Text("${meal.calories} kcal", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.Black)
        IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, "Delete Meal", tint = Color.Red.copy(alpha=0.7f)) }
    }
}

@Composable
fun WorkoutLogItem(workout: ConsumedWorkout, onDelete: () -> Unit) {
    val timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())
    val time = timeFormatter.format(workout.timestamp)

    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = workout.imageUrl,
            contentDescription = workout.name,
            modifier = Modifier.size(48.dp).clip(CircleShape),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = R.drawable.logo), // Fallback image
            error = painterResource(id = R.drawable.logo) // Error image
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(workout.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
            Text("Lúc: $time", fontSize = 13.sp, color = Color.DarkGray)
        }
        Text("${workout.caloriesBurned} kcal", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.Black)
        IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, "Delete Workout", tint = Color.Red.copy(alpha=0.7f)) }
    }
}
