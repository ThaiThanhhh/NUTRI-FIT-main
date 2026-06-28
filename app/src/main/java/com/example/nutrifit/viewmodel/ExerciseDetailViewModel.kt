package com.example.nutrifit.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutrifit.data.model.Exercise
import com.example.nutrifit.data.repository.ExerciseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class ExerciseDetailState {
    object Loading : ExerciseDetailState()
    data class Success(val exercise: Exercise) : ExerciseDetailState()
    data class Error(val message: String) : ExerciseDetailState()
}

class ExerciseDetailViewModel(private val exerciseId: String) : ViewModel() {

    private val exerciseRepository = ExerciseRepository()

    private val _exerciseDetailState = MutableStateFlow<ExerciseDetailState>(ExerciseDetailState.Loading)
    val exerciseDetailState: StateFlow<ExerciseDetailState> = _exerciseDetailState

    init {
        fetchExerciseDetails()
    }

    private fun fetchExerciseDetails() {
        viewModelScope.launch {
            exerciseRepository.getExerciseById(exerciseId).onSuccess {
                if (it != null) {
                    _exerciseDetailState.value = ExerciseDetailState.Success(it)
                } else {
                    _exerciseDetailState.value = ExerciseDetailState.Error("Exercise not found")
                }
            }.onFailure {
                _exerciseDetailState.value = ExerciseDetailState.Error(it.message ?: "Failed to fetch exercise details")
            }
        }
    }
}
