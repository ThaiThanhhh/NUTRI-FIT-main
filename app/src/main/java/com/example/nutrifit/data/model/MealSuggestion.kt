package com.example.nutrifit.data.model

data class MealSuggestion(
    val name: String,
    val ingredients: List<String>,
    val calories: Int,
    val imageRes: Int
)
