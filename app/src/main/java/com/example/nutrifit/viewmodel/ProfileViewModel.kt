package com.example.nutrifit.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutrifit.data.model.User
import com.example.nutrifit.data.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

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

    fun saveUserProfile(
        name: String,
        gender: String,
        age: String,
        height: String,
        weight: String
    ) {
        viewModelScope.launch {
            _saveState.value = SaveState.Loading
            val currentUser = auth.currentUser
            if (currentUser == null) {
                _saveState.value = SaveState.Error("User not logged in")
                return@launch
            }

            try {
                // Get existing user data first to retrieve the goal
                val existingUser = userRepository.getUser(currentUser.uid).getOrNull()

                // Calculate BMR and Calorie Goal with proper parsing
                val calorieGoal = calculateCalorieGoal(height, weight, age, gender, existingUser?.goal)

                val updatedUser = User(
                    id = currentUser.uid,
                    email = currentUser.email ?: "",
                    name = name,
                    gender = gender,
                    age = age,
                    height = height,
                    weight = weight,
                    goal = existingUser?.goal, // Keep the existing goal
                    calorieGoal = calorieGoal
                )

                userRepository.saveUser(updatedUser).getOrThrow()
                _saveState.value = SaveState.Success
            } catch (e: Exception) {
                _saveState.value = SaveState.Error(e.message ?: "Failed to save profile")
            }
        }
    }
    
    private fun parseAge(input: String): Int {
        return try {
            if (input.contains('-')) {
                val parts = input.split('-')
                val first = parts.getOrNull(0)?.filter { it.isDigit() }?.toIntOrNull() ?: 0
                val second = parts.getOrNull(1)?.filter { it.isDigit() }?.toIntOrNull() ?: first
                (first + second) / 2
            } else {
                input.filter { it.isDigit() }.toIntOrNull() ?: 0
            }
        } catch (e: Exception) {
            0
        }
    }

    private fun parseValue(input: String): Double {
        return input.filter { it.isDigit() }.toDoubleOrNull() ?: 0.0
    }

    private fun calculateCalorieGoal(heightStr: String, weightStr: String, ageStr: String, gender: String, goal: String?): Int {
        val heightInCm = parseValue(heightStr)
        val weightInKg = parseValue(weightStr)
        val ageInYears = parseAge(ageStr)

        if (heightInCm == 0.0 || weightInKg == 0.0 || ageInYears == 0) {
            return 2000 // Return a default value if parsing fails
        }

        // Harris-Benedict BMR Calculation
        val bmr = if (gender.equals("Nam", ignoreCase = true)) {
            88.362 + (13.397 * weightInKg) + (4.799 * heightInCm) - (5.677 * ageInYears)
        } else { // Nữ hoặc Khác
            447.593 + (9.247 * weightInKg) + (3.098 * heightInCm) - (4.330 * ageInYears)
        }

        // TDEE (Total Daily Energy Expenditure) calculation - assuming light activity level (1.375)
        val tdee = bmr * 1.375 

        // Adjust based on goal
        return when (goal) {
            "Giảm cân" -> (tdee - 500).toInt()
            "Tăng cơ / Tăng cân" -> (tdee + 500).toInt()
            else -> tdee.toInt() // "Ăn uống lành mạnh", "Giữ dáng / Duy trì sức khỏe"
        }
    }
}
