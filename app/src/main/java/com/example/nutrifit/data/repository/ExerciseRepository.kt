package com.example.nutrifit.data.repository

import com.example.nutrifit.data.model.Exercise
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ExerciseRepository {

    private val db = FirebaseFirestore.getInstance().collection("exercises")

    private fun documentToExercise(document: DocumentSnapshot): Exercise? {
        return try {
            val id = document.id
            val name = document.getString("name") ?: ""
            val description = document.getString("description") ?: ""
            val difficulty = document.getString("difficulty") ?: ""
            val imageUrl = document.getString("imageUrl") ?: ""
            val muscleGroup = document.getString("muscleGroup") ?: ""
            val targets = document.get("targets") as? List<String> ?: emptyList()
            val videoUrl = document.getString("videoUrl") ?: ""
            val caloriesBurned = document.getLong("caloriesBurned")?.toInt() ?: 0
            val reps = document.getString("reps") ?: ""

            Exercise(
                id = id,
                name = name,
                description = description,
                difficulty = difficulty,
                imageUrl = imageUrl,
                muscleGroup = muscleGroup,
                targets = targets,
                videoUrl = videoUrl,
                caloriesBurned = caloriesBurned,
                reps = reps
            )
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getAllExercises(): Result<List<Exercise>> {
        return try {
            val querySnapshot = db.get().await()
            val exercises = querySnapshot.documents.mapNotNull { document -> documentToExercise(document) }
            Result.success(exercises)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getExerciseById(exerciseId: String): Result<Exercise?> {
        return try {
            val document = db.document(exerciseId).get().await()
            val exercise = if (document.exists()) documentToExercise(document) else null
            Result.success(exercise)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getExercisesByTargets(targets: List<String>): Result<List<Exercise>> {
        return try {
            if (targets.isEmpty()) {
                return Result.success(emptyList())
            }
            val querySnapshot = db.whereArrayContainsAny("targets", targets).get().await()
            val exercises = querySnapshot.documents.mapNotNull { document -> documentToExercise(document) }
            Result.success(exercises)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
