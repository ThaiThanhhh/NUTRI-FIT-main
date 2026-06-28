package com.example.nutrifit.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutrifit.data.model.User
import com.example.nutrifit.data.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TargetViewModel : ViewModel() {

    private val userRepository = UserRepository()
    private val auth = FirebaseAuth.getInstance()

    private val _saveState = MutableStateFlow<SaveState>(SaveState.Idle)
    val saveState: StateFlow<SaveState> = _saveState

    sealed class SaveState {
        object Idle : SaveState()
        object Loading : SaveState()
        object Success : SaveState()
        data class Error(val message: String) : SaveState()
    }

    fun saveGoal(goal: String) {
        viewModelScope.launch {
            _saveState.value = SaveState.Loading
            val currentUser = auth.currentUser
            if (currentUser == null) {
                _saveState.value = SaveState.Error("User not logged in")
                return@launch
            }

            try {
                val userResult = userRepository.getUser(currentUser.uid)
                if (userResult.isSuccess) {
                    val user = userResult.getOrNull()!!
                    val calorieGoal = calculateCalorieGoal(user, goal)
                    val updatedUser = user.copy(goal = goal, calorieGoal = calorieGoal)
                    userRepository.saveUser(updatedUser).getOrThrow()
                    _saveState.value = SaveState.Success
                } else {
                    _saveState.value = SaveState.Error("User profile not found")
                }
            } catch (e: Exception) {
                _saveState.value = SaveState.Error(e.message ?: "Failed to save goal")
            }
        }
    }

    private fun calculateCalorieGoal(user: User, goal: String): Int {
        val weight = user.weight?.toDoubleOrNull() ?: 60.0
        val height = user.height?.toDoubleOrNull() ?: 165.0
        val age = user.age?.toIntOrNull() ?: 25

        // Standard BMR Calculation (Mifflin-St Jeor Equation)
        val bmr = if (user.gender == "Nam") {
            10 * weight + 6.25 * height - 5 * age + 5
        } else { // Nữ hoặc Khác
            10 * weight + 6.25 * height - 5 * age - 161
        }

        // TDEE Calculation (BMR * Activity Factor) - Assuming a sedentary lifestyle for now
        val tdee = bmr * 1.2

        return when (goal) {
            "Giảm cân" -> (tdee - 500).toInt()
            "Tăng cơ / Tăng cân" -> (tdee + 500).toInt()
            else -> tdee.toInt() // Maintain weight or eat healthy
        }
    }
}