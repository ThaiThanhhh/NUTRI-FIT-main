package com.example.nutrifit.data.model

data class User(
    val id: String = "",
    val email: String = "",
    val name: String = "",
    val height: String? = null,
    val weight: String? = null,
    val age: String? = null,
    val gender: String? = null,
    val goal: String? = null,
    val calorieGoal: Int? = null // Thêm trường mục tiêu calo
)