package com.example.nutrifit.viewmodel

import com.example.nutrifit.data.model.DailyIntake
import com.example.nutrifit.data.model.Exercise
import com.example.nutrifit.data.model.Meal
import com.example.nutrifit.data.model.User

sealed class AddMealState {
    object Idle : AddMealState()
    object Loading : AddMealState()
    object Success : AddMealState()
    data class Error(val message: String) : AddMealState()
}

sealed class MealsState {
    object Loading : MealsState()
    data class Success(val meals: List<Meal>) : MealsState()
    data class Error(val message: String) : MealsState()
}

sealed class MealDetailState {
    object Loading : MealDetailState()
    data class Success(val meal: Meal) : MealDetailState()
    data class Error(val message: String) : MealDetailState()
}

sealed class UserState {
    object Loading : UserState()
    data class Success(val user: User) : UserState()
    data class Error(val message: String) : UserState()
}

sealed class DailyIntakeState {
    object Loading : DailyIntakeState()
    data class Success(val intake: DailyIntake?) : DailyIntakeState()
    data class Error(val message: String) : DailyIntakeState()
}

sealed class SuggestedExercisesState {
    object Loading : SuggestedExercisesState()
    data class Success(val exercises: List<Exercise>) : SuggestedExercisesState()
    data class Error(val message: String) : SuggestedExercisesState()
}
