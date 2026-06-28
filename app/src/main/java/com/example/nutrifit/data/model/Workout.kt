package com.example.nutrifit.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.nutrifit.data.Converters

@Entity(tableName = "workouts")
@TypeConverters(Converters::class)
data class Workout(
    @PrimaryKey val id: String = "",
    val name: String = "",
    val description: String = "",
    val difficulty: String = "",
    val caloriesBurned: Int = 0,
    val muscleGroup: String = "",
    val reps: String = "",
    val imageUrl: String = "",
    val targets: List<String> = emptyList(),
    val videoUrl: String = ""
)
