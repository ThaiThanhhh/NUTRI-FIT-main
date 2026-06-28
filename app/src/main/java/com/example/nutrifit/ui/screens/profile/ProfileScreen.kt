package com.example.nutrifit.ui.screens.profile

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nutrifit.R
import com.example.nutrifit.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(onNextClicked: () -> Unit) {
    val viewModel: ProfileViewModel = viewModel()
    val context = LocalContext.current
    val saveState by viewModel.saveState.collectAsState()

    var name by remember { mutableStateOf("") }
    var selectedGender by remember { mutableStateOf("") }
    var selectedAge by remember { mutableStateOf("") }
    var selectedHeight by remember { mutableStateOf("") }
    var selectedWeight by remember { mutableStateOf("") }

    val genders = listOf("Nam", "Nữ", "Khác")
    val ages = listOf("Dưới 18", "18-25", "26-35", "36-45", "Trên 45")
    val heights = listOf("Dưới 1m50", "1m50 - 1m60", "1m61 - 1m70", "1m71 - 1m80", "Trên 1m80")
    val weights = listOf("Dưới 40kg", "40-50kg", "51-60kg", "61-70kg", "71-80kg", "Trên 80kg")

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White,
        focusedContainerColor = Color.Black.copy(alpha = 0.3f),
        unfocusedContainerColor = Color.Black.copy(alpha = 0.3f),
        cursorColor = Color.White,
        focusedBorderColor = Color.White,
        unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
        focusedLabelColor = Color.White,
        unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
        focusedTrailingIconColor = Color.White,
        unfocusedTrailingIconColor = Color.White.copy(alpha = 0.7f)
    )

    LaunchedEffect(saveState) {
        when (val state = saveState) {
            is ProfileViewModel.SaveState.Success -> {
                Toast.makeText(context, "Hồ sơ đã được lưu!", Toast.LENGTH_SHORT).show()
                onNextClicked()
            }
            is ProfileViewModel.SaveState.Error -> {
                Toast.makeText(context, "Lỗi: ${state.message}", Toast.LENGTH_LONG).show()
            }
            else -> Unit
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
            color = Color.Black.copy(alpha = 0.5f)
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
                    text = "Tạo hồ sơ của bạn",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Hãy cho chúng tôi biết thêm về bạn",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.padding(bottom = 32.dp)
                )
            }

            item {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Họ và tên") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    singleLine = true,
                    colors = textFieldColors
                )
            }

            item { DropdownInput(label = "Giới tính", options = genders, selectedOption = selectedGender, onOptionSelected = { selectedGender = it }, colors = textFieldColors) }
            item { DropdownInput(label = "Độ tuổi", options = ages, selectedOption = selectedAge, onOptionSelected = { selectedAge = it }, colors = textFieldColors) }
            item { DropdownInput(label = "Chiều cao", options = heights, selectedOption = selectedHeight, onOptionSelected = { selectedHeight = it }, colors = textFieldColors) }
            item { DropdownInput(label = "Cân nặng", options = weights, selectedOption = selectedWeight, onOptionSelected = { selectedWeight = it }, colors = textFieldColors) }

            item {
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = {
                        if (name.isNotBlank() && selectedGender.isNotBlank() && selectedAge.isNotBlank() && selectedHeight.isNotBlank() && selectedWeight.isNotBlank()) {
                            viewModel.saveUserProfile(name, selectedGender, selectedAge, selectedHeight, selectedWeight)
                        } else {
                            Toast.makeText(context, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text("Tiếp tục", fontSize = 18.sp)
                }
            }
        }

        if (saveState is ProfileViewModel.SaveState.Loading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownInput(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    colors: TextFieldColors
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
    ) {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            label = { Text(label) },
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            colors = colors
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}