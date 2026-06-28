package com.example.nutrifit.data.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.nutrifit.data.dao.ConsumedMealDao
import com.example.nutrifit.data.dao.ConsumedWorkoutDao
import com.example.nutrifit.data.dao.MealDao
import com.example.nutrifit.data.dao.WorkoutDao
import com.example.nutrifit.data.model.ConsumedMeal
import com.example.nutrifit.data.model.ConsumedWorkout
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class NutriFitRepository(
    private val mealDao: MealDao,
    private val workoutDao: WorkoutDao,
    private val consumedMealDao: ConsumedMealDao,
    private val consumedWorkoutDao: ConsumedWorkoutDao,
    private val firestore: FirebaseFirestore,
    private val context: Context
) {

    // Flows for observing data changes
    val allMeals: Flow<List<com.example.nutrifit.data.model.Meal>> = mealDao.getAll()
    val allWorkouts: Flow<List<com.example.nutrifit.data.model.Workout>> = workoutDao.getAll()
    val allConsumedMeals: Flow<List<ConsumedMeal>> = consumedMealDao.getAll()
    val allConsumedWorkouts: Flow<List<ConsumedWorkout>> = consumedWorkoutDao.getAll()

    // Functions to interact with Room
    suspend fun insertConsumedMeal(consumedMeal: ConsumedMeal) {
        consumedMealDao.insert(consumedMeal)
        syncData()
    }

    suspend fun insertConsumedWorkout(consumedWorkout: ConsumedWorkout) {
        consumedWorkoutDao.insert(consumedWorkout)
        syncData()
    }

    // Sync logic
    fun syncData() {
        if (isOnline()) {
            CoroutineScope(Dispatchers.IO).launch {
                syncConsumedMeals()
                syncConsumedWorkouts()
            }
        }
    }

    private suspend fun syncConsumedMeals() {
        val unsyncedMeals = consumedMealDao.getUnsynced()
        if (unsyncedMeals.isNotEmpty()) {
            val user = "test_user" // Replace with actual user ID
            try {
                val batch = firestore.batch()
                unsyncedMeals.forEach { meal ->
                    val docRef = firestore.collection("users").document(user)
                        .collection("consumedMeals").document(meal.id)
                    batch.set(docRef, meal)
                }
                batch.commit().await()
                consumedMealDao.markAsSynced(unsyncedMeals.map { it.id })
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    private suspend fun syncConsumedWorkouts() {
        val unsyncedWorkouts = consumedWorkoutDao.getUnsynced()
        if (unsyncedWorkouts.isNotEmpty()) {
            val user = "test_user" // Replace with actual user ID
            try {
                val batch = firestore.batch()
                unsyncedWorkouts.forEach { workout ->
                    val docRef = firestore.collection("users").document(user)
                        .collection("consumedWorkouts").document(workout.id)
                    batch.set(docRef, workout)
                }
                batch.commit().await()
                consumedWorkoutDao.markAsSynced(unsyncedWorkouts.map { it.id })
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    private fun isOnline(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork =
            connectivityManager.getNetworkCapabilities(network) ?: return false
        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            else -> false
        }
    }
}
