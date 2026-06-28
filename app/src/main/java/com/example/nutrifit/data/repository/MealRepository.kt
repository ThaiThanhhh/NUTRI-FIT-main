package com.example.nutrifit.data.repository

import com.example.nutrifit.data.model.Meal
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class MealRepository {

    private val db = FirebaseFirestore.getInstance().collection("meals")

    private fun documentToMeal(document: DocumentSnapshot): Meal? {
        return try {
            val id = (document.getLong("id")?.toInt()) ?: 0
            val name = document.getString("name") ?: ""
            val description = document.getString("description") ?: ""
            val imageResName = document.getString("imageRes") ?: "" // Get the name from Firestore
            val calories = (document.getLong("calories")?.toInt()) ?: 0
            val time = document.getString("time") ?: ""
            val category = document.getString("category") ?: ""
            val protein = (document.getLong("protein")?.toInt()) ?: 0
            val carbs = (document.getLong("carbs")?.toInt()) ?: 0
            val fat = (document.getLong("fat")?.toInt()) ?: 0
            val difficulty = document.getString("difficulty") ?: "Dễ"
            val instructions = document.getString("instructions") ?: ""
            val suitableGoals = document.get("suitableGoals") as? List<String> ?: emptyList()

            Meal(
                id = id,
                name = name,
                description = description,
                imageRes = imageResName, // Pass the name, not an Int ID
                calories = calories,
                time = time,
                category = category,
                protein = protein,
                carbs = carbs,
                fat = fat,
                difficulty = difficulty,
                instructions = instructions,
                suitableGoals = suitableGoals
            )
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getMealById(mealId: Int): Result<Meal?> {
        return try {
            val querySnapshot = db.whereEqualTo("id", mealId).limit(1).get().await()
            val document = querySnapshot.documents.firstOrNull()
            val meal = if (document != null) documentToMeal(document) else null
            Result.success(meal)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAllMeals(): Result<List<Meal>> {
        return try {
            val querySnapshot = db.get().await()
            val meals = querySnapshot.documents.mapNotNull { document -> documentToMeal(document) }
            Result.success(meals)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMealsByGoal(goal: String): Result<List<Meal>> {
        return try {
            val querySnapshot = db.whereArrayContains("suitableGoals", goal).get().await()
            val meals = querySnapshot.documents.mapNotNull { document -> documentToMeal(document) }
            Result.success(meals)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
