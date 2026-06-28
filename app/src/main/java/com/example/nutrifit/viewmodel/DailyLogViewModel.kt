package com.example.nutrifit.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutrifit.data.model.ConsumedMeal
import com.example.nutrifit.data.model.ConsumedWorkout
import com.example.nutrifit.data.repository.DailyIntakeRepository
import com.example.nutrifit.data.repository.WorkoutCompletionRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class DailyLogViewModel : ViewModel() {

    private val dailyIntakeRepository = DailyIntakeRepository()
    private val workoutCompletionRepository = WorkoutCompletionRepository()
    private val auth = FirebaseAuth.getInstance()

    private val _logState = MutableStateFlow<LogState>(LogState.Loading)
    val logState: StateFlow<LogState> = _logState

    private val weekOffset = MutableStateFlow(0)

    private val _weekDisplay = MutableStateFlow("")
    val weekDisplay: StateFlow<String> = _weekDisplay

    init {
        viewModelScope.launch {
            weekOffset.collect { offset ->
                val (startDate, endDate) = getWeekDateRange(offset)
                updateWeekDisplay(startDate, endDate)
                listenForLogChanges(startDate, endDate)
            }
        }
    }

    private fun listenForLogChanges(startDate: Date, endDate: Date) {
        viewModelScope.launch {
            _logState.value = LogState.Loading
            val userId = auth.currentUser?.uid
            if (userId == null) {
                _logState.value = LogState.Error("User not logged in")
                return@launch
            }

            dailyIntakeRepository.getIntakeForDateRange(userId, startDate, endDate).collect { result ->
                if (result.isSuccess) {
                    val weeklyIntake = result.getOrNull() ?: emptyList()
                    val allMeals = weeklyIntake.flatMap { it.consumedMeals }.sortedByDescending { it.consumedAt }
                    val allWorkouts = weeklyIntake.flatMap { it.consumedWorkouts }.sortedByDescending { it.timestamp }

                    _logState.value = LogState.Success(
                        meals = allMeals,
                        workouts = allWorkouts
                    )
                } else {
                    _logState.value = LogState.Error(result.exceptionOrNull()?.message ?: "Failed to fetch log")
                }
            }
        }
    }

    fun previousWeek() {
        weekOffset.value--
    }

    fun nextWeek() {
        if (weekOffset.value < 0) {
            weekOffset.value++
        }
    }

    private fun getWeekDateRange(offset: Int): Pair<Date, Date> {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.WEEK_OF_YEAR, offset)

        calendar.firstDayOfWeek = Calendar.MONDAY
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startDate = calendar.time

        calendar.add(Calendar.DAY_OF_YEAR, 6)
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val endDate = calendar.time

        return Pair(startDate, endDate)
    }

    private fun updateWeekDisplay(startDate: Date, endDate: Date) {
        val formatter = SimpleDateFormat("dd/MM", Locale.getDefault())
        _weekDisplay.value = "Tuần: ${formatter.format(startDate)} - ${formatter.format(endDate)}"
    }

    fun deleteMeal(meal: ConsumedMeal) {
        viewModelScope.launch {
            val userId = auth.currentUser?.uid ?: return@launch
            dailyIntakeRepository.removeConsumedMeal(userId, meal)
        }
    }

    fun deleteWorkout(workout: ConsumedWorkout) {
        viewModelScope.launch {
            val userId = auth.currentUser?.uid ?: return@launch

            // 1. Xóa khỏi nhật ký (daily_intakes)
            dailyIntakeRepository.removeConsumedWorkout(userId, workout)

            // 2. Xóa khỏi trạng thái hoàn thành (completed_workouts) để uncheck checkbox
            val localDate = Instant.ofEpochMilli(workout.timestamp.time).atZone(ZoneId.systemDefault()).toLocalDate()
            workoutCompletionRepository.unmarkWorkoutAsComplete(userId, workout.name, localDate)
        }
    }

    fun clearAllMeals() {
        viewModelScope.launch {
            val userId = auth.currentUser?.uid ?: return@launch
            if (_logState.value is LogState.Success) {
                val mealsToDelete = (_logState.value as LogState.Success).meals
                mealsToDelete.groupBy { it.consumedAt.day }.keys.forEach { day ->
                    val mealInDay = mealsToDelete.first { it.consumedAt.day == day }
                    dailyIntakeRepository.clearAllConsumedMealsForDate(userId, mealInDay.consumedAt)
                }
            }
        }
    }

    fun clearAllWorkouts() {
        viewModelScope.launch {
            val userId = auth.currentUser?.uid ?: return@launch
            if (_logState.value is LogState.Success) {
                val workoutsToDelete = (_logState.value as LogState.Success).workouts

                workoutsToDelete.forEach {
                    val localDate = Instant.ofEpochMilli(it.timestamp.time).atZone(ZoneId.systemDefault()).toLocalDate()
                    workoutCompletionRepository.unmarkWorkoutAsComplete(userId, it.name, localDate)
                }

                 workoutsToDelete.groupBy { it.timestamp.day }.keys.forEach { day ->
                    val workoutInDay = workoutsToDelete.first { it.timestamp.day == day }
                    dailyIntakeRepository.clearAllConsumedWorkoutsForDate(userId, workoutInDay.timestamp)
                }
            }
        }
    }
}

sealed class LogState {
    object Loading : LogState()
    data class Success(
        val meals: List<ConsumedMeal>,
        val workouts: List<ConsumedWorkout>
    ) : LogState()

    data class Error(val message: String) : LogState()
}
