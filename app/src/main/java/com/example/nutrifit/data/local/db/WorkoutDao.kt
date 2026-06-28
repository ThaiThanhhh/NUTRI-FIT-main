package com.example.nutrifit.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.nutrifit.data.model.Workout

@Dao
interface WorkoutDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(workouts: List<Workout>)

    @Query("SELECT * FROM workouts")
    suspend fun getAllWorkouts(): List<Workout>

    @Query("SELECT * FROM workouts WHERE id = :workoutId")
    suspend fun getWorkoutById(workoutId: String): Workout?

    @Query("DELETE FROM workouts")
    suspend fun deleteAll()
}
