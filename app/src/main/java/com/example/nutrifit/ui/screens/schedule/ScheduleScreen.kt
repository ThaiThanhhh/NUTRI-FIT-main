package com.example.nutrifit.ui.screens.schedule

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CheckCircle
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.nutrifit.R
import com.example.nutrifit.data.model.Exercise
import com.example.nutrifit.theme.NutriFitTheme
import com.example.nutrifit.viewmodel.CompletionState
import com.example.nutrifit.viewmodel.DailySchedule
import com.example.nutrifit.viewmodel.ScheduleState
import com.example.nutrifit.viewmodel.ScheduleViewModel
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(navController: NavController, onBackClick: () -> Unit) {
    val viewModel: ScheduleViewModel = viewModel()
    val scheduleState by viewModel.scheduleState.collectAsState()
    val completionState by viewModel.completionState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(completionState) {
        when (val state = completionState) {
            is CompletionState.Success -> {
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                viewModel.resetState()
            }
            is CompletionState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                viewModel.resetState()
            }
            else -> Unit
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        when (val state = scheduleState) {
            is ScheduleState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            is ScheduleState.Error -> {
                Text(text = state.message, modifier = Modifier.align(Alignment.Center))
            }
            is ScheduleState.Success -> {
                ScheduleContent(schedules = state.schedules, viewModel = viewModel, navController = navController, onBackClick = onBackClick)
            }
        }
    }
}

@Composable
fun ScheduleContent(schedules: List<DailySchedule>, viewModel: ScheduleViewModel, navController: NavController, onBackClick: () -> Unit) {
    val context = LocalContext.current
    Box(modifier = Modifier.fillMaxSize()) {
        var selectedDate by remember { mutableStateOf(LocalDate.now()) }
        val today = LocalDate.now()

        LaunchedEffect(key1 = selectedDate) {
            viewModel.updateScheduleForDate(selectedDate)
        }

        val todaySchedule = schedules.find { it.date == selectedDate }
        val dateFormatter = DateTimeFormatter.ofPattern("dd - MM - yyyy", Locale.getDefault())

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item { Spacer(modifier = Modifier.height(100.dp)) }

            item {
                Column(
                    Modifier.padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ScheduleHeader(
                        today = today,
                        selectedDate = selectedDate,
                        onDateChanged = { newDate -> selectedDate = newDate },
                        onWeekChanged = { direction ->
                            selectedDate = if (direction > 0) {
                                selectedDate.plusWeeks(1)
                            } else {
                                selectedDate.minusWeeks(1)
                            }
                        },
                        weeklySchedules = schedules,
                        dateFormatter = dateFormatter
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(8.dp)) }

            item {
                todaySchedule?.let {
                    Column(Modifier.padding(horizontal = 16.dp)) {
                        ScheduleDetailsCard(schedule = it, viewModel = viewModel, navController = navController)
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            item {
                todaySchedule?.let {
                    Column(Modifier.padding(horizontal = 16.dp)) {
                        DailyProgressFooter(schedule = it)
                        Spacer(modifier = Modifier.height(16.dp))
                        WeeklyProgressFooter(weeklySchedules = schedules)
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 40.dp, start = 16.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White)
                .clickable { onBackClick() }
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Quay lại",
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Quay lại",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF4CAF50)
                )
            }
        }
    }
}

@Composable
fun ScheduleHeader(
    today: LocalDate, selectedDate: LocalDate, onDateChanged: (LocalDate) -> Unit, onWeekChanged: (Int) -> Unit,
    weeklySchedules: List<DailySchedule>, dateFormatter: DateTimeFormatter
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Image(painter = painterResource(id = R.drawable.logo), contentDescription = "Nutri-Fit Logo", modifier = Modifier.size(80.dp))
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            Text(text = "NUTRI", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1AC9AC))
            Text(text = " - ", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Text(text = "FIT", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFF0044))
        }
        Spacer(modifier = Modifier.height(32.dp))
        Text(text = "Lịch tập của bạn", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        Spacer(modifier = Modifier.height(16.dp))
        Box(modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(Color.White).padding(horizontal = 16.dp, vertical = 8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(Color(0xFFE0E0E0)).clickable { onWeekChanged(-1) }, contentAlignment = Alignment.Center) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Tuần trước", tint = Color(0xFF4CAF50), modifier = Modifier.size(24.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = selectedDate.format(dateFormatter), fontSize = 16.sp, color = Color.Black, fontWeight = FontWeight.Medium, modifier = Modifier.widthIn(min = 120.dp), textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.width(12.dp))
                Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(Color(0xFFE0E0E0)).clickable { onWeekChanged(1) }, contentAlignment = Alignment.Center) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Tuần sau", tint = Color(0xFF4CAF50), modifier = Modifier.size(24.dp))
                }
            }
        }

        Box(modifier = Modifier.height(52.dp).padding(vertical = 4.dp), contentAlignment = Alignment.Center) {
             if (selectedDate != today) {
                Button(
                    onClick = { onDateChanged(today) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800), contentColor = Color.White),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = "Quay về hôm nay", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            weeklySchedules.forEach { schedule ->
                val isScheduleComplete = schedule.exercises.isNotEmpty() && schedule.exercises.all { it.isCompleted }
                DayItemNew(schedule = schedule, isToday = schedule.date == today, isSelected = schedule.date == selectedDate, isCompleted = isScheduleComplete, today = today, onSelected = { onDateChanged(schedule.date) })
            }
        }
    }
}


@Composable
fun ScheduleDetailsCard(schedule: DailySchedule, viewModel: ScheduleViewModel, navController: NavController) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = schedule.scheduleName,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(16.dp))
            if (schedule.exercises.isEmpty()) {
                Text("Hôm nay là ngày nghỉ! Hãy nghỉ ngơi và phục hồi.", modifier = Modifier.padding(vertical = 16.dp), color = Color.Gray)
            } else {
                schedule.exercises.forEachIndexed { index, exercise ->
                    val isEnabled = schedule.date == LocalDate.now()
                    ExerciseItem(
                        exercise = exercise,
                        onCheckedChange = {
                            viewModel.handleCheckChanged(exercise, it, schedule.date)
                        },
                        onClick = {
                            navController.navigate("workout_detail/${exercise.name}")
                        },
                        isEnabled = isEnabled
                    )
                    if (index < schedule.exercises.size - 1) {
                        HorizontalDivider(color = Color(0xFFE0E0E0), thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun DayItemNew(schedule: DailySchedule, isToday: Boolean, isSelected: Boolean, isCompleted: Boolean, today: LocalDate, onSelected: () -> Unit) {
    val dayOfWeekMap = mapOf(DayOfWeek.MONDAY to "T2", DayOfWeek.TUESDAY to "T3", DayOfWeek.WEDNESDAY to "T4", DayOfWeek.THURSDAY to "T5", DayOfWeek.FRIDAY to "T6", DayOfWeek.SATURDAY to "T7", DayOfWeek.SUNDAY to "CN")
    val fullDayOfWeekMap = mapOf(DayOfWeek.MONDAY to "Thứ 2", DayOfWeek.TUESDAY to "Thứ 3", DayOfWeek.WEDNESDAY to "Thứ 4", DayOfWeek.THURSDAY to "Thứ 5", DayOfWeek.FRIDAY to "Thứ 6", DayOfWeek.SATURDAY to "Thứ 7", DayOfWeek.SUNDAY to "CN")
    val iconMap = mapOf(DayOfWeek.MONDAY to R.drawable.nguc_tay, DayOfWeek.TUESDAY to R.drawable.lung_vai, DayOfWeek.WEDNESDAY to R.drawable.chan_mong, DayOfWeek.THURSDAY to R.drawable.vai_core, DayOfWeek.FRIDAY to R.drawable.tay_bung, DayOfWeek.SATURDAY to R.drawable.cardio, DayOfWeek.SUNDAY to R.drawable.nghi_ngoi)
    val dateFormatter = DateTimeFormatter.ofPattern("dd")
    val scheduleNameLines = schedule.scheduleName.split("&").map { it.trim() }

    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(48.dp)) {
        Text(text = dayOfWeekMap[schedule.date.dayOfWeek] ?: "", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = Color.Gray)
        Spacer(modifier = Modifier.height(4.dp))
        Box(modifier = Modifier.width(48.dp).height(140.dp).clip(RoundedCornerShape(12.dp)).background(if (isSelected) Color(0xFF4361EE) else Color.White).clickable { onSelected() }.padding(vertical = 8.dp, horizontal = 4.dp), contentAlignment = Alignment.TopCenter) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = schedule.date.format(dateFormatter), fontWeight = FontWeight.Bold, color = if (isSelected) Color.White else Color.Black, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = fullDayOfWeekMap[schedule.date.dayOfWeek] ?: "", fontSize = 9.sp, color = if (isSelected) Color.White else Color.Gray, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(4.dp))
                Box(modifier = Modifier.size(28.dp).clip(CircleShape).background(if (isSelected) Color.White.copy(alpha = 0.3f) else Color(0xFFE8F5E9)), contentAlignment = Alignment.Center) {
                    Image(painter = painterResource(id = iconMap[schedule.date.dayOfWeek] ?: R.drawable.baitap), contentDescription = "Icon bài tập", modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.height(4.dp))
                if (schedule.scheduleName == "Nghỉ ngơi") {
                    Text(text = "Nghỉ", fontSize = 8.sp, color = if (isSelected) Color.White else Color.Black, textAlign = TextAlign.Center, lineHeight = 10.sp, maxLines = 1)
                } else {
                    scheduleNameLines.forEachIndexed { index, line ->
                        Text(text = line, fontSize = 8.sp, color = if (isSelected) Color.White else Color.Black, textAlign = TextAlign.Center, lineHeight = 10.sp, maxLines = 1)
                        if (index < scheduleNameLines.size - 1) {
                            Text(text = "&", fontSize = 8.sp, color = if (isSelected) Color.White else Color.Black, textAlign = TextAlign.Center, lineHeight = 10.sp)
                        }
                    }
                }
            }
            if (isToday && !isSelected) {
                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color.Red).align(Alignment.TopEnd).offset(x = (-2).dp, y = 2.dp))
            }
            if (isCompleted && schedule.date.isBefore(today.plusDays(1))) {
                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = "Đã hoàn thành", tint = Color(0xFF4CAF50), modifier = Modifier.size(16.dp).align(Alignment.BottomEnd).offset(x = (-2).dp, y = (-2).dp))
            }
        }
    }
}

@Composable
fun ExerciseItem(
    exercise: Exercise,
    onCheckedChange: (Boolean) -> Unit,
    onClick: () -> Unit,
    isEnabled: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = exercise.imageUrl,
            contentDescription = exercise.name,
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = R.drawable.baitap),
            error = painterResource(id = R.drawable.baitap)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = exercise.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = if (exercise.isCompleted) Color.Gray else Color.Black,
                textDecoration = if (exercise.isCompleted) TextDecoration.LineThrough else null
            )
            Text(
                text = exercise.description,
                fontSize = 14.sp,
                color = Color.Gray,
                maxLines = 2
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Checkbox(
            checked = exercise.isCompleted,
            onCheckedChange = onCheckedChange,
            enabled = isEnabled,
            colors = CheckboxDefaults.colors(
                checkedColor = Color(0xFF4CAF50),
                uncheckedColor = Color(0xFF4361EE),
                disabledCheckedColor = Color(0xFF4CAF50).copy(alpha = 0.4f),
                disabledUncheckedColor = Color.Gray.copy(alpha = 0.4f)
            )
        )
    }
}

@Composable
fun DailyProgressFooter(schedule: DailySchedule) {
    if (schedule.exercises.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Color(0xFF4CAF50)).padding(vertical = 12.dp, horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "NGÀY NGHỈ", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        }
    } else {
        val completedCount = schedule.exercises.count { it.isCompleted }
        val totalCount = schedule.exercises.size
        val text = if (completedCount == totalCount) "ĐÃ HOÀN THÀNH" else "${completedCount}/${totalCount} BÀI TẬP"
        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Color(0xFF4CAF50)).padding(vertical = 12.dp, horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = text, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun WeeklyProgressFooter(weeklySchedules: List<DailySchedule>) {
    val totalCompletedExercises = weeklySchedules.sumOf { schedule -> schedule.exercises.count { it.isCompleted } }
    val totalExercises = weeklySchedules.sumOf { it.exercises.size }
    val progress = if (totalExercises > 0) totalCompletedExercises.toFloat() / totalExercises else 0f
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Tiến độ tuần", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
            Text(text = "${(progress * 100).toInt()}%", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
        }
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(5.dp)), color = Color(0xFF4CAF50), trackColor = Color(0xFFE0E0E0))
    }
}

@Preview(showBackground = true)
@Composable
fun ScheduleScreenPreview() {
    NutriFitTheme {
        // ScheduleScreen(onBackClick = {})
    }
}
