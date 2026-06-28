package com.example.nutrifit.data.model

data class TargetState(
    val title: String,
    val description: String,
    val isSelected: Boolean = false,
    val caloriesGoal: Int = 3000,
    val currentCalories: Int = 1950,
    val progress: Float = 0.65f,
    val weeklyData: List<Float> = listOf(2.9f, 3.0f, 5.1f, 7.1f, 3.2f, 3.0f, 0f)
)
