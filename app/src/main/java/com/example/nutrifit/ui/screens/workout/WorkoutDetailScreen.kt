package com.example.nutrifit.ui.screens.workout

import android.net.Uri
import android.widget.VideoView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.nutrifit.data.model.Workout
import com.example.nutrifit.viewmodel.WorkoutViewModel
import com.example.nutrifit.viewmodel.WorkoutsState

@Composable
fun WorkoutDetailScreen(
    workoutId: String,
    navController: NavController,
    workoutViewModel: WorkoutViewModel
) {
    // Lắng nghe trạng thái từ ViewModel
    val workoutsState by workoutViewModel.workoutsState.collectAsState()

    // Tìm bài tập dựa trên ID
    var workout by remember(workoutsState, workoutId) {
        mutableStateOf<Workout?>(null)
    }

    LaunchedEffect(workoutsState, workoutId) {
        if (workoutsState is WorkoutsState.Success) {
            workout = (workoutsState as WorkoutsState.Success).workouts.values
                .flatten()
                .find { it.name == workoutId }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFFF7F7F7) // Màu nền xám nhẹ
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            when (workoutsState) {
                is WorkoutsState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is WorkoutsState.Error -> {
                    // Hiển thị lỗi nếu không thể tải dữ liệu
                    Text(
                        text = (workoutsState as WorkoutsState.Error).message,
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.Center).padding(16.dp)
                    )
                }
                is WorkoutsState.Success -> {
                    if (workout != null) {
                        val scrollState = rememberScrollState()
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(scrollState)
                        ) {
                            HeaderSection(workout!!, navController)
                            Spacer(modifier = Modifier.height(16.dp))
                            BodySection(workout!!)
                        }
                    } else {
                        // Hiển thị nếu không tìm thấy bài tập với ID tương ứng
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Không tìm thấy thông tin bài tập.", color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HeaderSection(workout: Workout, navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
    ) {
        AsyncImage(
            model = workout.imageUrl,
            contentDescription = workout.name,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)),
            contentScale = ContentScale.Crop
        )
        // Lớp phủ màu tối để chữ dễ đọc hơn
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Color.Black.copy(alpha = 0.3f),
                    RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                )
        )

        // Nút Back
        Card(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .padding(start = 16.dp, top = 40.dp)
                .width(100.dp)
                .height(36.dp),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.5f))
        ) {
            Row(
                modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White, modifier = Modifier.height(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Quay về", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }
        }

        // Tên bài tập
        Text(
            text = workout.name,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        )
    }
}

@Composable
private fun BodySection(workout: Workout) {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Thông tin tổng quan (Nhóm cơ, độ khó, mục tiêu)
        InfoCard(workout)

        // Mô tả chi tiết
        DescriptionCard(workout.description)

        // Video hướng dẫn
        VideoCard(workout)
    }
}

@Composable
private fun InfoCard(workout: Workout) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Thông tin chi tiết", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.Black)
            Spacer(modifier = Modifier.height(16.dp))
            InfoChipRow(icon = Icons.Default.FitnessCenter, title = "Nhóm cơ", value = workout.muscleGroup)
            Spacer(modifier = Modifier.height(12.dp))
            InfoChipRow(icon = Icons.Default.Shield, title = "Độ khó", value = workout.difficulty)
            Spacer(modifier = Modifier.height(12.dp))
            InfoChipRow(icon = Icons.Default.FitnessCenter, title = "Reps", value = workout.reps)
            Spacer(modifier = Modifier.height(12.dp))
            InfoChipRow(icon = Icons.Default.FitnessCenter, title = "Calories Burned", value = "${workout.caloriesBurned} kcal")
            Spacer(modifier = Modifier.height(12.dp))
            InfoChipRow(icon = Icons.Default.Bookmark, title = "Mục tiêu", value = workout.targets.joinToString(), isChipRow = true)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun InfoChipRow(icon: ImageVector, title: String, value: String, isChipRow: Boolean = false) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .size(20.dp)
                .padding(top = 4.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = "$title:",
            fontWeight = FontWeight.SemiBold,
            color = Color.Black,
            modifier = Modifier.padding(top = 4.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        if (isChipRow) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                value.split(",").forEach {
                    Text(
                        text = it.trim(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.DarkGray,
                        modifier = Modifier
                            .background(Color(0xFFEFEFEF), RoundedCornerShape(4.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        } else {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.DarkGray,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
private fun DescriptionCard(description: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Mô tả", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.Black)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = description, style = MaterialTheme.typography.bodyLarge, color = Color.DarkGray, lineHeight = 24.sp)
        }
    }
}

@Composable
private fun VideoCard(workout: Workout) {
    val context = LocalContext.current
    val videoResId = context.resources.getIdentifier(workout.videoUrl, "raw", context.packageName)
    if (videoResId != 0) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Video hướng dẫn", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.Black)
                Spacer(modifier = Modifier.height(16.dp))

                val videoUri = Uri.parse("android.resource://${context.packageName}/$videoResId")
                var isPlaying by remember { mutableStateOf(false) }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp) // Tăng chiều cao
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black), // Nền đen khi video chưa tải
                    contentAlignment = Alignment.Center
                ) {
                    AndroidView(
                        factory = {
                            VideoView(it).apply {
                                setVideoURI(videoUri)
                                setOnPreparedListener { mp -> mp.isLooping = true }
                            }
                        },
                        update = { videoView ->
                            if (isPlaying) videoView.start() else videoView.pause()
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable(enabled = isPlaying) { // Chỉ cho phép click pause khi đang phát
                                isPlaying = false
                            }
                    )

                    // Nút Play ở giữa, chỉ hiển thị khi video chưa phát
                    if (!isPlaying) {
                        IconButton(
                            onClick = { isPlaying = true },
                            modifier = Modifier
                                .size(72.dp)
                                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Play Video",
                                tint = Color.White,
                                modifier = Modifier.size(48.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
