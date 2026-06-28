package com.example.nutrifit.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.nutrifit.data.model.ConsumedMeal
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface ConsumedMealDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(consumedMeal: ConsumedMeal)

    @Query("SELECT * FROM consumed_meals WHERE consumedAt BETWEEN :startDate AND :endDate")
    fun getByDate(startDate: Date, endDate: Date): Flow<List<ConsumedMeal>>

    @Query("SELECT * FROM consumed_meals WHERE synced = 0")
    suspend fun getUnsynced(): List<ConsumedMeal>

    @Query("UPDATE consumed_meals SET synced = 1 WHERE id IN (:ids)")
    suspend fun markAsSynced(ids: List<String>)

    @Query("SELECT * FROM consumed_meals")
    fun getAll(): Flow<List<ConsumedMeal>>
}
