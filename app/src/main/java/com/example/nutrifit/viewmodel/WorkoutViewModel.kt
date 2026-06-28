package com.example.nutrifit.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutrifit.data.local.db.AppDatabase
import com.example.nutrifit.data.model.Workout
import com.example.nutrifit.data.repository.WorkoutRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class WorkoutsState {
    object Loading : WorkoutsState()
    data class Success(val workouts: Map<String, List<Workout>>) : WorkoutsState()
    data class Error(val message: String) : WorkoutsState()
}

class WorkoutViewModel(application: Application) : AndroidViewModel(application) {

    private val workoutRepository: WorkoutRepository

    private val _workoutsState = MutableStateFlow<WorkoutsState>(WorkoutsState.Loading)
    val workoutsState: StateFlow<WorkoutsState> = _workoutsState

    init {
        val workoutDao = AppDatabase.getDatabase(application).workoutDao()
        workoutRepository = WorkoutRepository(workoutDao)
        fetchAllWorkouts()
    }

    private fun fetchAllWorkouts() {
        viewModelScope.launch {
            _workoutsState.value = WorkoutsState.Loading
            workoutRepository.getWorkouts().onSuccess { workouts ->
                if (workouts.isEmpty()) {
                    _workoutsState.value = WorkoutsState.Error("Không tìm thấy bài tập nào. Vui lòng kiểm tra dữ liệu trên Firestore.")
                } else {
                    val groupedWorkouts = workouts.groupBy { workout -> workout.muscleGroup }
                    _workoutsState.value = WorkoutsState.Success(groupedWorkouts)
                }
            }.onFailure {
                _workoutsState.value = WorkoutsState.Error(it.message ?: "Failed to fetch workouts.")
            }
        }
    }
}
