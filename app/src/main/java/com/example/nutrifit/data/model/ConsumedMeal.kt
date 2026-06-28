package com.example.nutrifit.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.nutrifit.data.Converters
import java.util.Date
import java.util.UUID

@Entity(tableName = "consumed_meals")
@TypeConverters(Converters::class)
data class ConsumedMeal(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val mealId: Int = 0,
    val name: String = "",
    val calories: Int = 0,
    val consumedAt: Date = Date(),
    val mealType: String = "",
    val imageRes: String = "",
    var synced: Boolean = false
)
