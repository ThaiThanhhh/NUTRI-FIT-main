package com.example.nutrifit.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.nutrifit.data.dao.ConsumedMealDao
import com.example.nutrifit.data.dao.ConsumedWorkoutDao
import com.example.nutrifit.data.dao.MealDao
import com.example.nutrifit.data.dao.WorkoutDao
import com.example.nutrifit.data.model.ConsumedMeal
import com.example.nutrifit.data.model.ConsumedWorkout
import com.example.nutrifit.data.model.Meal
import com.example.nutrifit.data.model.Workout

@Database(
    entities = [Meal::class, Workout::class, ConsumedMeal::class, ConsumedWorkout::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class NutriFitDatabase : RoomDatabase() {

    abstract fun mealDao(): MealDao
    abstract fun workoutDao(): WorkoutDao
    abstract fun consumedMealDao(): ConsumedMealDao
    abstract fun consumedWorkoutDao(): ConsumedWorkoutDao

    companion object {
        @Volatile
        private var INSTANCE: NutriFitDatabase? = null

        fun getDatabase(context: Context): NutriFitDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NutriFitDatabase::class.java,
                    "nutrifit_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
