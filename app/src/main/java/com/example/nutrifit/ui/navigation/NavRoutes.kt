package com.example.nutrifit.ui.navigation

object NavRoutes {
    const val Onboarding = "onboarding"
    const val Login = "login"
    const val Login2 = "login2"
    const val Home = "home"
    const val Meal = "meal"
    const val Scan = "scan"
    const val Workout = "workout"
    const val Map = "map"
    const val Profile = "profile"
    const val DailyLog = "dailylog"

    const val Setting = "setting" // đang làm setting
    const val Target = "target"
    const val Register = "register"
    const val ForgotPw = "forgotpw"
    const val ForgotPw2 = "forgotpw2"
    const val Schedule = "schedule"
    const val MealDetail = "mealdetail"
    const val WORKOUT_DETAIL = "workout_detail"
    const val Terms = "terms"

    // ✨ THÊM MỚI - Email Verification
    const val EmailVerification = "email_verification"

    // Helper function để tạo route với email parameter
    fun emailVerificationRoute(email: String): String {
        return "$EmailVerification/$email"
    }
}
