package com.example.nutrifit.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.nutrifit.data.Converters
import java.util.Date
import java.util.UUID

@Entity(tableName = "consumed_workouts")
@TypeConverters(Converters::class)
data class ConsumedWorkout(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val workoutId: Int = 0,
    val name: String = "",
    val caloriesBurned: Int = 0,
    val timestamp: Date = Date(),
    val imageUrl: String = "",
    var synced: Boolean = false
)
