package com.example.nutrifit.ui.screens.workout

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.nutrifit.R
import com.example.nutrifit.data.model.Workout
import com.example.nutrifit.ui.navigation.NavRoutes
import com.example.nutrifit.viewmodel.WorkoutViewModel
import com.example.nutrifit.viewmodel.WorkoutsState

// Helper function để lấy icon cho nhóm cơ
@DrawableRes
fun getMuscleGroupIcon(groupName: String): Int {
    return when (groupName) {
        "Ngực" -> R.drawable.ic_muscle_nguc
        "Lưng" -> R.drawable.ic_muscle_lung
        "Chân" -> R.drawable.ic_muscle_duitruoc // Lấy đại diện cho chân
        "Vai" -> R.drawable.ic_muscle_vai
        "Bụng" -> R.drawable.ic_muscle_bung
        "Toàn thân" -> R.drawable.ic_muscle_toanthan
        else -> R.drawable.logo // Icon mặc định
    }
}

@Composable
fun WorkoutScreen(navController: NavController) {
    val workoutViewModel: WorkoutViewModel = viewModel()
    val workoutsState by workoutViewModel.workoutsState.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.White
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            when (val state = workoutsState) {
                is WorkoutsState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is WorkoutsState.Error -> {
                    Text(state.message, color = Color.Red, modifier = Modifier.align(Alignment.Center).padding(16.dp))
                }
                is WorkoutsState.Success -> {
                    WorkoutContent(groupedWorkouts = state.workouts, navController = navController)
                }
            }
        }
    }
}

@Composable
fun WorkoutContent(groupedWorkouts: Map<String, List<Workout>>, navController: NavController) {
    val muscleGroups = groupedWorkouts.keys.toList()
    var selectedGroup by remember { mutableStateOf(muscleGroups.firstOrNull() ?: "") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        item {
            Text(
                text = "Chọn nhóm cơ",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        item {
            MuscleGroupSelection(
                groups = muscleGroups,
                selectedGroup = selectedGroup,
                onGroupSelected = { group -> selectedGroup = group }
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            Text(
                text = "Bài tập cho $selectedGroup",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        items(groupedWorkouts[selectedGroup] ?: emptyList()) { workout ->
            WorkoutCard(workout = workout) {
                navController.navigate("${NavRoutes.WORKOUT_DETAIL}/${workout.name}")
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun MuscleGroupSelection(groups: List<String>, selectedGroup: String, onGroupSelected: (String) -> Unit) {
    val chunkedGroups = groups.chunked(3)

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        chunkedGroups.forEach { rowGroups ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowGroups.forEach { group ->
                    MuscleChip(name = group, isSelected = group == selectedGroup, onClick = { onGroupSelected(group) }, modifier = Modifier.weight(1f))
                }
                // Thêm Spacer để các hàng không đủ 3 item vẫn căn chỉnh đúng
                if (rowGroups.size < 3) {
                    repeat(3 - rowGroups.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun MuscleChip(name: String, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val backgroundColor = if (isSelected) Color(0xFFE3F2FD) else Color.White
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else Color(0xFFE0E0E0)

    Card(
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().clickable(onClick = onClick),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = getMuscleGroupIcon(name)),
                contentDescription = name,
                tint = Color.Unspecified, // Sửa ở đây
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = name, color = Color.Black, fontWeight = FontWeight.Medium, fontSize = 14.sp)
        }
    }
}

@Composable
fun WorkoutCard(workout: Workout, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFFEEEEEE), RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(0.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.height(130.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = workout.imageUrl,
                contentDescription = workout.name,
                modifier = Modifier
                    .size(130.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp)),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier.padding(16.dp).fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = workout.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Độ khó: ${workout.difficulty}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Reps: ${workout.reps}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${workout.caloriesBurned} kcal",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Red,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Row {
                    workout.targets.take(2).forEach {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                }
            }
        }
    }
}
