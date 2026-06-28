package com.example.nutrifit.data.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

/**
 * Represents a user's total dietary intake and workout for a single day.
 */
data class DailyIntake(
    val id: String = "",
    val userId: String = "",
    @ServerTimestamp
    val date: Date? = null,
    val totalCalories: Int = 0, 
    val consumedMeals: List<ConsumedMeal> = emptyList(),
    val consumedWorkouts: List<ConsumedWorkout> = emptyList()
) {
    // Helper function to calculate total calories consumed
    fun getTotalCaloriesConsumed(): Int = consumedMeals.sumOf { it.calories }

    // Helper function to calculate total calories burned
    fun getTotalCaloriesBurned(): Int = consumedWorkouts.sumOf { it.caloriesBurned }

    // Helper function to calculate net calories
    fun getNetCalories(): Int = getTotalCaloriesConsumed() - getTotalCaloriesBurned()
}
