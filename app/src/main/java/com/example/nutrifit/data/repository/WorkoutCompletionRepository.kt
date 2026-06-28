package com.example.nutrifit.data.repository

import com.example.nutrifit.data.model.CompletedWorkout
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneId
import java.util.Date

class WorkoutCompletionRepository {

    private fun getUserWorkoutsCollection(userId: String) = 
        FirebaseFirestore.getInstance().collection("users").document(userId).collection("completed_workouts")

    suspend fun markWorkoutAsComplete(userId: String, completedWorkout: CompletedWorkout): Result<Unit> {
        return try {
            getUserWorkoutsCollection(userId).add(completedWorkout).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Nâng cấp để sử dụng Flow
    fun getCompletedWorkoutsFlow(userId: String, date: LocalDate): Flow<List<CompletedWorkout>> = callbackFlow {
        val startOfDay = Date(date.atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000)
        val endOfDay = Date(date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000)

        val listener = getUserWorkoutsCollection(userId)
            .whereGreaterThanOrEqualTo("completedAt", startOfDay)
            .whereLessThan("completedAt", endOfDay)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    close(e)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val workouts = snapshot.toObjects(CompletedWorkout::class.java)
                    trySend(workouts)
                }
            }
        
        awaitClose { listener.remove() }
    }

    suspend fun unmarkWorkoutAsComplete(userId: String, workoutName: String, date: LocalDate): Result<Unit> {
        return try {
            val startOfDay = Date(date.atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000)
            val endOfDay = Date(date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000)

            val querySnapshot = getUserWorkoutsCollection(userId)
                .whereEqualTo("workoutName", workoutName)
                .whereGreaterThanOrEqualTo("completedAt", startOfDay)
                .whereLessThan("completedAt", endOfDay)
                .get()
                .await()

            if (!querySnapshot.isEmpty) {
                val batch = FirebaseFirestore.getInstance().batch()
                querySnapshot.documents.forEach { document ->
                    batch.delete(document.reference)
                }
                batch.commit().await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
