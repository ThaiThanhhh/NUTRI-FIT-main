package com.example.nutrifit.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.nutrifit.data.Converters

@Entity(tableName = "meals")
@TypeConverters(Converters::class)
data class Meal(
    @PrimaryKey val id: Int,
    val name: String,
    val description: String,
    val category: String,
    val difficulty: String,
    val calories: Int,
    val protein: Int,
    val carbs: Int,
    val fat: Int,
    val time: String,
    val imageRes: String,
    val instructions: String,
    val suitableGoals: List<String>
)
