package com.example.nutrifit.data.repository

import com.example.nutrifit.data.local.db.WorkoutDao
import com.example.nutrifit.data.model.Workout
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.Date

class WorkoutRepository(private val workoutDao: WorkoutDao) {
    private val db = FirebaseFirestore.getInstance()
    private val workoutsCollection = db.collection("exercises")
    private val completedWorkoutsCollection = db.collection("completed_workouts")
    private val dailyIntakeRepository = DailyIntakeRepository()

    suspend fun getWorkouts(): Result<List<Workout>> {
        return try {
            val localWorkouts = workoutDao.getAllWorkouts()
            if (localWorkouts.isNotEmpty()) {
                Result.success(localWorkouts)
            } else {
                val snapshot = workoutsCollection.get().await()
                val firestoreWorkouts = snapshot.documents.mapNotNull {
                    it.toObject(Workout::class.java)?.copy(id = it.id)
                }
                if (firestoreWorkouts.isNotEmpty()) {
                    workoutDao.insertAll(firestoreWorkouts)
                }
                Result.success(firestoreWorkouts)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getWorkoutById(workoutId: String): Result<Workout?> {
        return try {
            var workout = workoutDao.getWorkoutById(workoutId)
            if (workout == null) {
                val document = workoutsCollection.document(workoutId).get().await()
                workout = document.toObject(Workout::class.java)?.copy(id = document.id)
            }
            Result.success(workout)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addCompletedWorkout(userId: String, workoutId: String): Result<Unit> {
        return try {
            val data = hashMapOf(
                "userId" to userId,
                "workoutId" to workoutId,
                "completedAt" to Date()
            )
            completedWorkoutsCollection.add(data).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun removeCompletedWorkout(userId: String, workoutId: String): Result<Unit> {
        return try {
            val workoutResult = getWorkoutById(workoutId)
            if (workoutResult.isSuccess) {
                workoutResult.getOrNull()?.let {
                    dailyIntakeRepository.removeConsumedWorkoutByName(userId, it.name, Date())
                }
            }

            val querySnapshot = completedWorkoutsCollection
                .whereEqualTo("userId", userId)
                .whereEqualTo("workoutId", workoutId)
                .get()
                .await()

            if (!querySnapshot.isEmpty) {
                for (document in querySnapshot.documents) {
                    completedWorkoutsCollection.document(document.id).delete().await()
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCompletedWorkouts(userId: String): Result<List<String>> {
        return try {
            val snapshot = completedWorkoutsCollection.whereEqualTo("userId", userId).get().await()
            val completedIds = snapshot.documents.mapNotNull {
                it.getString("workoutId")
            }
            Result.success(completedIds)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
