package com.example.nutrifit.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutrifit.R
import com.example.nutrifit.data.model.DailyIntake
import com.example.nutrifit.data.model.Exercise
import com.example.nutrifit.data.model.Meal
import com.example.nutrifit.data.repository.DailyIntakeRepository
import com.example.nutrifit.data.repository.ExerciseRepository
import com.example.nutrifit.data.repository.MealRepository
import com.example.nutrifit.data.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val userRepository: UserRepository = UserRepository()
    private val dailyIntakeRepository: DailyIntakeRepository = DailyIntakeRepository()
    private val mealRepository: MealRepository = MealRepository()
    private val exerciseRepository: ExerciseRepository = ExerciseRepository()


    private val _userState = MutableStateFlow<UserState>(UserState.Loading)
    val userState: StateFlow<UserState> = _userState

    private val _dailyIntakeState = MutableStateFlow<DailyIntakeState>(DailyIntakeState.Loading)
    val dailyIntakeState: StateFlow<DailyIntakeState> = _dailyIntakeState

    private val _weeklyIntakeState = MutableStateFlow<List<DailyIntake>>(emptyList())
    val weeklyIntakeState: StateFlow<List<DailyIntake>> = _weeklyIntakeState

    private val _suggestedMealsState = MutableStateFlow<MealsState>(MealsState.Loading)
    val suggestedMealsState: StateFlow<MealsState> = _suggestedMealsState

    private val _suggestedExercisesState = MutableStateFlow<SuggestedExercisesState>(SuggestedExercisesState.Loading)
    val suggestedExercisesState: StateFlow<SuggestedExercisesState> = _suggestedExercisesState


    init {
        fetchUserDataAndSuggestions()
        listenForDailyIntakeChanges()
    }

    fun refresh() {
        fetchUserDataAndSuggestions()
        listenForDailyIntakeChanges()
    }

    private fun fetchUserDataAndSuggestions() {
        viewModelScope.launch {
            _userState.value = UserState.Loading
            _suggestedMealsState.value = MealsState.Loading
            _suggestedExercisesState.value = SuggestedExercisesState.Loading


            val firebaseUser = auth.currentUser
            if (firebaseUser != null) {
                userRepository.getUser(firebaseUser.uid).onSuccess { user ->
                    _userState.value = UserState.Success(user)
                    user.goal?.let {
                        fetchMealSuggestions(it)
                        fetchExerciseSuggestions(it)
                    }
                }.onFailure {
                    _userState.value = UserState.Error(it.message ?: "Failed to fetch user data.")
                }
            } else {
                val error = "No user logged in."
                _userState.value = UserState.Error(error)
                _suggestedMealsState.value = MealsState.Error(error)
                _suggestedExercisesState.value = SuggestedExercisesState.Error(error)
            }
        }
    }

    private fun listenForDailyIntakeChanges() {
        viewModelScope.launch {
            _dailyIntakeState.value = DailyIntakeState.Loading
            val firebaseUser = auth.currentUser
            if (firebaseUser != null) {
                val calendar = Calendar.getInstance()
                calendar.firstDayOfWeek = Calendar.MONDAY
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                val startOfWeek = calendar.apply { set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0) }.time

                calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
                val endOfWeek = calendar.apply { set(Calendar.HOUR_OF_DAY, 23); set(Calendar.MINUTE, 59); set(Calendar.SECOND, 59) }.time

                dailyIntakeRepository.getIntakeForDateRange(firebaseUser.uid, startOfWeek, endOfWeek).collect { result ->
                    result.onSuccess { weeklyIntakes ->
                        _weeklyIntakeState.value = weeklyIntakes
                        val todayIntake = weeklyIntakes.find { it.date?.let { date -> isSameDay(date, Date()) } ?: false }
                        _dailyIntakeState.value = DailyIntakeState.Success(todayIntake)
                    }.onFailure { exception ->
                        _dailyIntakeState.value = DailyIntakeState.Error(exception.message ?: "Failed to fetch daily intake.")
                    }
                }
            } else {
                _dailyIntakeState.value = DailyIntakeState.Error("No user logged in.")
            }
        }
    }

    private fun isSameDay(date1: Date, date2: Date): Boolean {
        val cal1 = Calendar.getInstance().apply { time = date1 }
        val cal2 = Calendar.getInstance().apply { time = date2 }
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
               cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }


    private fun fetchMealSuggestions(goal: String) {
        viewModelScope.launch {
            mealRepository.getMealsByGoal(goal).onSuccess { meals ->
                _suggestedMealsState.value = MealsState.Success(meals)
            }.onFailure { e ->
                _suggestedMealsState.value = MealsState.Error(e.message ?: "Failed to fetch suggestions")
            }
        }
    }

    private fun fetchExerciseSuggestions(goal: String) {
        viewModelScope.launch {
            val userTargets = goal.split("/").map { it.trim() }

            // Lấy danh sách bài tập dựa trên mục tiêu của người dùng
            exerciseRepository.getExercisesByTargets(userTargets).onSuccess { userGoalExercises ->
                if (userGoalExercises.isNotEmpty()) {
                    _suggestedExercisesState.value = SuggestedExercisesState.Success(userGoalExercises)
                } else {
                    // Nếu không có, lấy các bài tập cho các mục tiêu chung
                    val defaultTargets = listOf("Giảm cân", "Tăng cơ")
                    exerciseRepository.getExercisesByTargets(defaultTargets).onSuccess { defaultExercises ->
                        _suggestedExercisesState.value = SuggestedExercisesState.Success(defaultExercises)
                    }.onFailure { e ->
                        _suggestedExercisesState.value = SuggestedExercisesState.Error(e.message ?: "Failed to fetch default suggestions")
                    }
                }
            }.onFailure { e ->
                _suggestedExercisesState.value = SuggestedExercisesState.Error(e.message ?: "Failed to fetch user goal suggestions")
            }
        }
    }
}
