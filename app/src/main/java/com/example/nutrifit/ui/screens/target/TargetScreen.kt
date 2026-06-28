package com.example.nutrifit.ui.screens.target

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nutrifit.R
import com.example.nutrifit.viewmodel.TargetViewModel

@Composable
fun TargetScreen(
    onBack: () -> Unit,
    onNextClicked: () -> Unit
) {
    val viewModel: TargetViewModel = viewModel()
    val context = LocalContext.current
    val saveState by viewModel.saveState.collectAsState()

    var selectedGoal by remember { mutableStateOf("") }
    val goals = listOf(
        "Ăn uống lành mạnh",
        "Giảm cân",
        "Tăng cơ / Tăng cân",
        "Giữ dáng / Duy trì sức khỏe"
    )

    LaunchedEffect(saveState) {
        when (val state = saveState) {
            is TargetViewModel.SaveState.Success -> {
                Toast.makeText(context, "Mục tiêu đã được lưu!", Toast.LENGTH_SHORT).show()
                onNextClicked()
            }
            is TargetViewModel.SaveState.Error -> {
                Toast.makeText(context, "Lỗi: ${state.message}", Toast.LENGTH_LONG).show()
            }
            else -> {}
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.profile_register),
            contentDescription = "Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.Black.copy(alpha = 0.6f) // Slightly darker overlay for better contrast
        ) { }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            contentPadding = WindowInsets.systemBars.asPaddingValues(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            item {
                Text(
                    text = "Mục tiêu của bạn là gì?",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Chúng tôi sẽ cá nhân hóa lộ trình tập luyện và chế độ ăn uống theo mục tiêu của bạn.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 32.dp)
                )
            }

            items(goals) { goal ->
                GoalOptionCard(goal, selectedGoal == goal) {
                    selectedGoal = goal
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = { viewModel.saveGoal(selectedGoal) },
                    enabled = selectedGoal.isNotBlank(),
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text("Tiếp tục", fontSize = 18.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(onClick = onBack) {
                    Text("Quay lại", color = Color.White.copy(alpha = 0.8f))
                }
            }
        }

        if (saveState is TargetViewModel.SaveState.Loading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}

@Composable
fun GoalOptionCard(goal: String, isSelected: Boolean, onClick: () -> Unit) {
    val description = when (goal) {
        "Ăn uống lành mạnh" -> "Xây dựng thói quen ăn uống khoa học và bền vững"
        "Giảm cân" -> "Đốt mỡ, thon gọn một cách khoa học"
        "Tăng cơ / Tăng cân" -> "Phát triển cơ bắp, tăng cân lành mạnh"
        "Giữ dáng / Duy trì sức khỏe" -> "Cân bằng tập luyện và dinh dưỡng luôn khỏe mạnh"
        else -> ""
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.large,
        border = BorderStroke(
            width = 2.dp,
            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.5f)
        ),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = goal,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.8f),
                    lineHeight = 18.sp
                )
            }
            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(
                    selectedColor = MaterialTheme.colorScheme.primary,
                    unselectedColor = Color.White.copy(alpha = 0.7f)
                )
            )
        }
    }
}