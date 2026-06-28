package com.example.nutrifit.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.nutrifit.data.model.ConsumedWorkout
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface ConsumedWorkoutDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(consumedWorkout: ConsumedWorkout)

    @Query("SELECT * FROM consumed_workouts WHERE timestamp BETWEEN :startDate AND :endDate")
    fun getByDate(startDate: Date, endDate: Date): Flow<List<ConsumedWorkout>>

    @Query("SELECT * FROM consumed_workouts WHERE synced = 0")
    suspend fun getUnsynced(): List<ConsumedWorkout>

    @Query("UPDATE consumed_workouts SET synced = 1 WHERE id IN (:ids)")
    suspend fun markAsSynced(ids: List<String>)

    @Query("SELECT * FROM consumed_workouts")
    fun getAll(): Flow<List<ConsumedWorkout>>
}
