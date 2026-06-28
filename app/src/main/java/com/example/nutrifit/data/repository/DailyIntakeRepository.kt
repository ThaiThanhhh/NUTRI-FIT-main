package com.example.nutrifit.data.repository

import com.example.nutrifit.data.model.ConsumedMeal
import com.example.nutrifit.data.model.ConsumedWorkout
import com.example.nutrifit.data.model.DailyIntake
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.Date
import java.util.Calendar

class DailyIntakeRepository {

    private val db = FirebaseFirestore.getInstance().collection("daily_intakes")

    fun getIntakeForDateRange(userId: String, startDate: Date, endDate: Date): Flow<Result<List<DailyIntake>>> = callbackFlow {
        val query = db
            .whereEqualTo("userId", userId)
            .whereGreaterThanOrEqualTo("date", startDate)
            .whereLessThanOrEqualTo("date", endDate)
            .orderBy("date", Query.Direction.DESCENDING)

        val listener = query.addSnapshotListener { snapshot, e ->
            if (e != null) {
                trySend(Result.failure(e))
                return@addSnapshotListener
            }

            if (snapshot == null) {
                trySend(Result.success(emptyList()))
            } else {
                val weeklyIntakes = snapshot.documents.mapNotNull {
                    it.toObject(DailyIntake::class.java)?.copy(id = it.id)
                }
                trySend(Result.success(weeklyIntakes))
            }
        }

        awaitClose { listener.remove() }
    }


    private suspend fun getDailyIntake(userId: String, date: Date): Result<DailyIntake?> {
        return try {
            val calendar = Calendar.getInstance()
            calendar.time = date
            val startOfDay = calendar.apply { set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0) }.time
            val endOfDay = calendar.apply { set(Calendar.HOUR_OF_DAY, 23); set(Calendar.MINUTE, 59); set(Calendar.SECOND, 59) }.time

            val query = db
                .whereEqualTo("userId", userId)
                .whereGreaterThanOrEqualTo("date", startOfDay)
                .whereLessThanOrEqualTo("date", endOfDay)
                .limit(1)
                .get()
                .await()

            val document = query.documents.firstOrNull()
            val dailyIntake = document?.toObject(DailyIntake::class.java)?.copy(id = document.id)

            Result.success(dailyIntake)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addConsumedMeal(userId: String, meal: ConsumedMeal): Result<Unit> {
        return try {
            val today = Date()
            val existingIntakeResult = getDailyIntake(userId, today)

            if (existingIntakeResult.isSuccess) {
                val existingIntake = existingIntakeResult.getOrNull()
                if (existingIntake != null && existingIntake.id.isNotEmpty()) {
                    db.document(existingIntake.id).update("consumedMeals", FieldValue.arrayUnion(meal)).await()
                } else {
                    val newIntake = DailyIntake(userId = userId, date = today, consumedMeals = listOf(meal))
                    db.add(newIntake).await()
                }
                Result.success(Unit)
            } else {
                Result.failure(existingIntakeResult.exceptionOrNull() ?: Exception("Failed to check for existing intake."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addConsumedWorkout(userId: String, workout: ConsumedWorkout): Result<Unit> {
        return try {
            val today = Date()
            val existingIntakeResult = getDailyIntake(userId, today)

            if (existingIntakeResult.isSuccess) {
                val existingIntake = existingIntakeResult.getOrNull()
                if (existingIntake != null && existingIntake.id.isNotEmpty()) {
                    db.document(existingIntake.id).update("consumedWorkouts", FieldValue.arrayUnion(workout)).await()
                } else {
                    val newIntake = DailyIntake(userId = userId, date = today, consumedWorkouts = listOf(workout))
                    db.add(newIntake).await()
                }
                Result.success(Unit)
            } else {
                Result.failure(existingIntakeResult.exceptionOrNull() ?: Exception("Failed to check for existing intake."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun removeConsumedMeal(userId: String, meal: ConsumedMeal): Result<Unit> {
        return try {
            val mealDate = meal.consumedAt
            val intakeResult = getDailyIntake(userId, mealDate)

            if (intakeResult.isSuccess) {
                val intake = intakeResult.getOrNull()
                if (intake != null && intake.id.isNotEmpty()) {
                    db.document(intake.id).update("consumedMeals", FieldValue.arrayRemove(meal)).await()
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("No intake document found for that day to remove meal from."))
                }
            } else {
                Result.failure(intakeResult.exceptionOrNull() ?: Exception("Failed to find intake to remove meal."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun removeConsumedWorkout(userId: String, workout: ConsumedWorkout): Result<Unit> {
        return try {
            val workoutDate = workout.timestamp
            val intakeResult = getDailyIntake(userId, workoutDate)

            if (intakeResult.isSuccess) {
                val intake = intakeResult.getOrNull()
                if (intake != null && intake.id.isNotEmpty()) {
                    db.document(intake.id).update("consumedWorkouts", FieldValue.arrayRemove(workout)).await()
                }
                Result.success(Unit)
            } else {
                Result.failure(intakeResult.exceptionOrNull() ?: Exception("Failed to find intake to remove workout."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun removeConsumedWorkoutByName(userId: String, workoutName: String, date: Date): Result<Unit> {
        return try {
            val existingIntakeResult = getDailyIntake(userId, date)

            if (existingIntakeResult.isSuccess) {
                val existingIntake = existingIntakeResult.getOrNull()
                if (existingIntake != null && existingIntake.id.isNotEmpty() && existingIntake.consumedWorkouts.isNotEmpty()) {
                    val currentWorkouts = existingIntake.consumedWorkouts.toMutableList()
                    val workoutRemoved = currentWorkouts.removeIf { it.name == workoutName }

                    if (workoutRemoved) {
                        db.document(existingIntake.id).update("consumedWorkouts", currentWorkouts).await()
                    }
                }
                Result.success(Unit)
            } else {
                Result.failure(existingIntakeResult.exceptionOrNull() ?: Exception("Failed to get daily intake to remove workout."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun clearAllConsumedMealsForDate(userId: String, date: Date): Result<Unit> {
        return try {
            val existingIntakeResult = getDailyIntake(userId, date)

            if (existingIntakeResult.isSuccess) {
                val existingIntake = existingIntakeResult.getOrNull()
                if (existingIntake != null && existingIntake.id.isNotEmpty()) {
                    db.document(existingIntake.id).update("consumedMeals", emptyList<ConsumedMeal>()).await()
                }
                Result.success(Unit)
            } else {
                Result.failure(existingIntakeResult.exceptionOrNull() ?: Exception("Failed to check for existing intake."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun clearAllConsumedWorkoutsForDate(userId: String, date: Date): Result<Unit> {
        return try {
            val existingIntakeResult = getDailyIntake(userId, date)

            if (existingIntakeResult.isSuccess) {
                val existingIntake = existingIntakeResult.getOrNull()
                if (existingIntake != null && existingIntake.id.isNotEmpty()) {
                    db.document(existingIntake.id).update("consumedWorkouts", emptyList<ConsumedWorkout>()).await()
                }
                Result.success(Unit)
            } else {
                Result.failure(existingIntakeResult.exceptionOrNull() ?: Exception("Failed to check for existing intake."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
