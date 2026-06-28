package com.example.nutrifit.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutrifit.data.model.CompletedWorkout
import com.example.nutrifit.data.model.ConsumedWorkout
import com.example.nutrifit.data.model.Exercise
import com.example.nutrifit.data.repository.DailyIntakeRepository
import com.example.nutrifit.data.repository.ExerciseRepository
import com.example.nutrifit.data.repository.WorkoutCompletionRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.threeten.bp.DateTimeUtils
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneId
import java.util.Date
import java.util.UUID

data class DailySchedule(
    val date: LocalDate,
    val scheduleName: String,
    val exercises: SnapshotStateList<Exercise>
)

sealed class ScheduleState {
    object Loading : ScheduleState()
    data class Success(val schedules: List<DailySchedule>) : ScheduleState()
    data class Error(val message: String) : ScheduleState()
}

sealed class CompletionState {
    object Idle : CompletionState()
    data class Success(val message: String) : CompletionState()
    data class Error(val message: String) : CompletionState()
}

class ScheduleViewModel : ViewModel() {

    private val workoutCompletionRepository = WorkoutCompletionRepository()
    private val exerciseRepository = ExerciseRepository()
    private val dailyIntakeRepository = DailyIntakeRepository()
    private val auth = FirebaseAuth.getInstance()

    private val _scheduleState = MutableStateFlow<ScheduleState>(ScheduleState.Loading)
    val scheduleState: StateFlow<ScheduleState> = _scheduleState.asStateFlow()

    private val _completionState = MutableStateFlow<CompletionState>(CompletionState.Idle)
    val completionState: StateFlow<CompletionState> = _completionState.asStateFlow()

    private var allExercises: List<Exercise> = emptyList()

    init {
        loadAllExercises()
    }

    private fun loadAllExercises() {
        viewModelScope.launch {
            _scheduleState.value = ScheduleState.Loading
            exerciseRepository.getAllExercises().onSuccess { exercises ->
                allExercises = exercises
                updateScheduleForDate(LocalDate.now())
            }.onFailure {
                _scheduleState.value = ScheduleState.Error(it.message ?: "Lỗi khi tải bài tập")
            }
        }
    }

    fun updateScheduleForDate(date: LocalDate) {
        if (allExercises.isEmpty()) return

        viewModelScope.launch {
            val weeklySchedules = generateWeekSchedule(date)
            syncScheduleState(weeklySchedules)
            _scheduleState.value = ScheduleState.Success(weeklySchedules)
        }
    }

    private fun generateWeekSchedule(dateInWeek: LocalDate): List<DailySchedule> {
        val startOfWeek = dateInWeek.with(DayOfWeek.MONDAY)
        return (0..6).map { i ->
            val currentDate = startOfWeek.plusDays(i.toLong())
            val (scheduleName, dailyExercises) = when (currentDate.dayOfWeek) {
                DayOfWeek.MONDAY -> "Ngực & Tay" to allExercises.filter { it.muscleGroup == "Ngực" || it.muscleGroup == "Tay trước" }
                DayOfWeek.TUESDAY -> "Lưng & Vai" to allExercises.filter { it.muscleGroup == "Lưng" || it.muscleGroup == "Vai" }
                DayOfWeek.WEDNESDAY -> "Chân & Mông" to allExercises.filter { it.muscleGroup == "Chân" || it.muscleGroup == "Mông" }
                DayOfWeek.THURSDAY -> "Vai & Core" to allExercises.filter { it.muscleGroup == "Vai" || it.muscleGroup == "Bụng" }
                DayOfWeek.FRIDAY -> "Tay & Bụng" to allExercises.filter { it.muscleGroup == "Tay sau" || it.muscleGroup == "Bụng" }
                DayOfWeek.SATURDAY -> "Cardio" to allExercises.filter { it.muscleGroup == "Toàn thân" }
                DayOfWeek.SUNDAY -> "Nghỉ ngơi" to emptyList()
            }
            DailySchedule(currentDate, scheduleName, mutableStateListOf<Exercise>().apply { addAll(dailyExercises.map { it.copy() }) })
        }
    }

    private fun syncScheduleState(weeklySchedules: List<DailySchedule>) {
        val userId = auth.currentUser?.uid ?: return

        weeklySchedules.forEach { dailySchedule ->
            if (dailySchedule.exercises.isNotEmpty()) {
                viewModelScope.launch {
                    workoutCompletionRepository.getCompletedWorkoutsFlow(userId, dailySchedule.date).collectLatest { completedWorkouts ->
                        val updatedExercises = dailySchedule.exercises.map { exercise ->
                            exercise.copy(isCompleted = completedWorkouts.any { it.workoutName == exercise.name })
                        }
                        dailySchedule.exercises.clear()
                        dailySchedule.exercises.addAll(updatedExercises)
                    }
                }
            }
        }
    }

    fun handleCheckChanged(exercise: Exercise, isChecked: Boolean, date: LocalDate) {
        if (date != LocalDate.now()) {
            viewModelScope.launch {
                _completionState.value = CompletionState.Error("Chỉ được phép chỉnh sửa bài tập trong ngày hôm nay.")
            }
            return
        }
        // --- Cập nhật giao diện ngay lập tức ---
        val currentState = _scheduleState.value
        if (currentState is ScheduleState.Success) {
            currentState.schedules.find { it.date == date }?.let { dailySchedule ->
                val exerciseIndex = dailySchedule.exercises.indexOfFirst { it.name == exercise.name }
                if (exerciseIndex != -1) {
                    val updatedExercise = dailySchedule.exercises[exerciseIndex].copy(isCompleted = isChecked)
                    dailySchedule.exercises[exerciseIndex] = updatedExercise
                }
            }
        }
        // --- Kết thúc cập nhật giao diện ---

        viewModelScope.launch {
            val userId = auth.currentUser?.uid ?: run {
                _completionState.value = CompletionState.Error("Bạn chưa đăng nhập!")
                return@launch
            }

            if (isChecked) {
                val consumedWorkout = ConsumedWorkout(name = exercise.name, caloriesBurned = exercise.caloriesBurned, imageUrl = exercise.imageUrl, timestamp = Date())
                dailyIntakeRepository.addConsumedWorkout(userId, consumedWorkout)
                
                val completedWorkout = CompletedWorkout(
                    id = UUID.randomUUID().toString(),
                    userId = userId,
                    workoutName = exercise.name,
                    muscleGroup = exercise.muscleGroup,
                    caloriesBurned = exercise.caloriesBurned,
                    imageUrl = exercise.imageUrl,
                    completedAt = DateTimeUtils.toDate(date.atStartOfDay(ZoneId.systemDefault()).toInstant())
                )
                workoutCompletionRepository.markWorkoutAsComplete(userId, completedWorkout).onSuccess {
                     _completionState.value = CompletionState.Success("Đã hoàn thành: ${exercise.name}")
                }.onFailure {
                     _completionState.value = CompletionState.Error(it.message ?: "Lỗi khi lưu")
                }

            } else {
                dailyIntakeRepository.removeConsumedWorkoutByName(userId, exercise.name, DateTimeUtils.toDate(date.atStartOfDay(ZoneId.systemDefault()).toInstant()))
                workoutCompletionRepository.unmarkWorkoutAsComplete(userId, exercise.name, date).onSuccess {
                    _completionState.value = CompletionState.Success("Đã bỏ hoàn thành: ${exercise.name}")
                }.onFailure {
                     _completionState.value = CompletionState.Error(it.message ?: "Lỗi khi bỏ lưu")
                }
            }
        }
    }

    fun resetState() {
        _completionState.value = CompletionState.Idle
    }
}
