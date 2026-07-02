package com.example.nutrifit.utils

import com.google.firebase.firestore.FirebaseFirestore

object DataSeeder {
    private val db = FirebaseFirestore.getInstance()

    fun seedAllData() {
        seedMeals()
        seedExercises()
    }

    private fun seedMeals() {
        val mealsCollection = db.collection("meals")
        val meals = listOf(
            mapOf("id" to 1, "name" to "Bò Bít Tết", "category" to "Cơm", "calories" to 700, "protein" to 45, "carbs" to 50, "fat" to 30, "time" to "25 phút", "difficulty" to "Trung bình", "imageRes" to "bo_bit_tet", "description" to "Bò bít tết giàu đạm.", "instructions" to "1. Áp chảo bò.", "suitableGoals" to listOf("Tăng cơ / Tăng cân")),
            mapOf("id" to 2, "name" to "Sinh Tố Bơ", "category" to "Nước uống", "calories" to 400, "protein" to 5, "carbs" to 20, "fat" to 35, "time" to "5 phút", "difficulty" to "Dễ", "imageRes" to "stbo", "description" to "Sinh tố bơ béo ngậy.", "instructions" to "1. Xay bơ với sữa.", "suitableGoals" to listOf("Duy trì cân nặng", "Tăng cơ / Tăng cân"))
        )
        for (meal in meals) {
            mealsCollection.document(meal["id"].toString()).set(meal)
        }
    }

    private fun seedExercises() {
        val exCol = db.collection("exercises")
        val exercises = listOf(
            // NHÓM NGỰC
            mapOf("name" to "Hít đất", "videoUrl" to "push_up", "imageUrl" to "nguc_tay", "muscleGroup" to "Ngực", "caloriesBurned" to 100, "targets" to listOf("Tăng cơ"), "difficulty" to "Dễ", "reps" to "3 hiệp x 15", "description" to "Tập ngực."),
            mapOf("name" to "Nâng tạ ngực", "videoUrl" to "dumbbell_bench_press", "imageUrl" to "bench", "muscleGroup" to "Ngực", "caloriesBurned" to 130, "targets" to listOf("Tăng cơ"), "difficulty" to "Trung bình", "reps" to "4 hiệp x 10 cái", "description" to "Phát triển ngực."),
            mapOf("name" to "Đẩy tạ đòn", "videoUrl" to "barbell_squat", "imageUrl" to "bench", "muscleGroup" to "Ngực", "caloriesBurned" to 150, "targets" to listOf("Tăng cơ"), "difficulty" to "Khó", "reps" to "4 hiệp x 10", "description" to "Phát triển độ dày cơ ngực."),

            // NHÓM LƯNG
            mapOf("name" to "Hít xà", "videoUrl" to "pull_up", "imageUrl" to "pullup", "muscleGroup" to "Lưng", "caloriesBurned" to 120, "targets" to listOf("Tăng cơ"), "difficulty" to "Khó", "reps" to "3 hiệp x 8", "description" to "Tập lưng."),
            mapOf("name" to "Cử tạ", "videoUrl" to "deadlift", "imageUrl" to "deadlift", "muscleGroup" to "Lưng", "caloriesBurned" to 250, "targets" to listOf("Tăng cân"), "difficulty" to "Khó", "reps" to "3 hiệp x 5", "description" to "Sức mạnh toàn thân."),

            // NHÓM CHÂN
            mapOf("name" to "Squat", "videoUrl" to "barbell_squat", "imageUrl" to "squat", "muscleGroup" to "Chân", "caloriesBurned" to 150, "targets" to listOf("Tăng cơ"), "difficulty" to "Dễ", "reps" to "3 hiệp x 20", "description" to "Tập chân."),
            mapOf("name" to "Bắc cầu mông", "videoUrl" to "glute_bridge", "imageUrl" to "chan_mong", "muscleGroup" to "Chân", "caloriesBurned" to 80, "targets" to listOf("Duy trì"), "difficulty" to "Dễ", "reps" to "3 hiệp x 20", "description" to "Kích hoạt cơ mông."),
            mapOf("name" to "Chùng chân", "videoUrl" to "lunges", "imageUrl" to "chan_mong", "muscleGroup" to "Chân", "caloriesBurned" to 110, "targets" to listOf("Giảm cân"), "difficulty" to "Dễ", "reps" to "3 hiệp x 12", "description" to "Mông đùi linh hoạt."),

            // NHÓM VAI
            mapOf("name" to "Đẩy tạ vai", "videoUrl" to "overhead_pressoverhead_press", "imageUrl" to "vai_core", "muscleGroup" to "Vai", "caloriesBurned" to 110, "targets" to listOf("Tăng cơ"), "difficulty" to "Trung bình", "reps" to "3 hiệp x 12", "description" to "Tập vai."),

            // NHÓM BỤNG
            mapOf("name" to "Plank", "videoUrl" to "plank", "imageUrl" to "tay_bung", "muscleGroup" to "Bụng", "caloriesBurned" to 50, "targets" to listOf("Cơ bụng"), "difficulty" to "Dễ", "reps" to "60 giây", "description" to "Tập bụng."),

            // NHÓM TOÀN THÂN
            mapOf("name" to "Nhảy dây", "videoUrl" to "jumping_jacks", "imageUrl" to "cardio", "muscleGroup" to "Toàn thân", "caloriesBurned" to 200, "targets" to listOf("Cardio"), "difficulty" to "Dễ", "reps" to "15 phút", "description" to "Cardio.")
        )
        for (ex in exercises) {
            exCol.document(ex["name"].toString()).set(ex)
        }
    }
}
