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
import java.text.SimpleDateFormat
import java.util.Locale

class DailyIntakeRepository {

    private val db = FirebaseFirestore.getInstance().collection("daily_intakes")

    fun getIntakeForDateRange(userId: String, startDate: Date, endDate: Date): Flow<Result<List<DailyIntake>>> = callbackFlow {
        // Chỉ lọc theo userId để tránh phải tạo Index phức tạp cho Date
        val query = db.whereEqualTo("userId", userId)

        val listener = query.addSnapshotListener { snapshot, e ->
            if (e != null) {
                trySend(Result.failure(e))
                return@addSnapshotListener
            }

            if (snapshot == null) {
                trySend(Result.success(emptyList()))
            } else {
                val allIntakes = snapshot.documents.mapNotNull {
                    it.toObject(DailyIntake::class.java)?.copy(id = it.id)
                }
                // Tự lọc theo ngày tháng ngay tại App (Client-side filtering)
                val filteredIntakes = allIntakes.filter { intake ->
                    intake.date != null && !intake.date.before(startDate) && !intake.date.after(endDate)
                }.sortedByDescending { it.date }
                
                trySend(Result.success(filteredIntakes))
            }
        }

        awaitClose { listener.remove() }
    }

    private fun getDailyId(userId: String, date: Date): String {
        val sdf = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        return "${userId}_${sdf.format(date)}"
    }

    suspend fun addConsumedMeal(userId: String, meal: ConsumedMeal): Result<Unit> {
        return try {
            val docId = getDailyId(userId, Date())
            val docRef = db.document(docId)
            val doc = docRef.get().await()

            if (doc.exists()) {
                docRef.update("consumedMeals", FieldValue.arrayUnion(meal)).await()
            } else {
                val newIntake = DailyIntake(userId = userId, date = Date(), consumedMeals = listOf(meal))
                docRef.set(newIntake).await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addConsumedWorkout(userId: String, workout: ConsumedWorkout): Result<Unit> {
        return try {
            val docId = getDailyId(userId, Date())
            val docRef = db.document(docId)
            val doc = docRef.get().await()

            if (doc.exists()) {
                docRef.update("consumedWorkouts", FieldValue.arrayUnion(workout)).await()
            } else {
                val newIntake = DailyIntake(userId = userId, date = Date(), consumedWorkouts = listOf(workout))
                docRef.set(newIntake).await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun removeConsumedMeal(userId: String, meal: ConsumedMeal): Result<Unit> {
        return try {
            val docId = getDailyId(userId, meal.consumedAt)
            db.document(docId).update("consumedMeals", FieldValue.arrayRemove(meal)).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun removeConsumedWorkout(userId: String, workout: ConsumedWorkout): Result<Unit> {
        return try {
            val docId = getDailyId(userId, workout.timestamp)
            db.document(docId).update("consumedWorkouts", FieldValue.arrayRemove(workout)).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun removeConsumedWorkoutByName(userId: String, workoutName: String, date: Date): Result<Unit> {
        return try {
            val docId = getDailyId(userId, date)
            val docRef = db.document(docId)
            val doc = docRef.get().await()
            val intake = doc.toObject(DailyIntake::class.java)

            if (intake != null && intake.consumedWorkouts.isNotEmpty()) {
                val newList = intake.consumedWorkouts.filter { it.name != workoutName }
                docRef.update("consumedWorkouts", newList).await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun clearAllConsumedMealsForDate(userId: String, date: Date): Result<Unit> {
        return try {
            val docId = getDailyId(userId, date)
            db.document(docId).update("consumedMeals", emptyList<ConsumedMeal>()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun clearAllConsumedWorkoutsForDate(userId: String, date: Date): Result<Unit> {
        return try {
            val docId = getDailyId(userId, date)
            db.document(docId).update("consumedWorkouts", emptyList<ConsumedWorkout>()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
