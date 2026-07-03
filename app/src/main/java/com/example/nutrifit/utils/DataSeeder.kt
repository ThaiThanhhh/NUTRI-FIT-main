package com.example.nutrifit.utils

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

object DataSeeder {
    private val db = FirebaseFirestore.getInstance()

    fun seedAllData() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                clearCollection("meals")
                clearCollection("exercises")
                seedMeals()
                seedExercises()
            } catch (e: Exception) {
                Log.e("DataSeeder", "Error: ${e.message}")
            }
        }
    }

    private suspend fun clearCollection(path: String) {
        val snapshot = db.collection(path).get().await()
        val batch = db.batch()
        for (doc in snapshot.documents) batch.delete(doc.reference)
        batch.commit().await()
    }

    private suspend fun seedMeals() {
        val col = db.collection("meals")
        val allGoals = listOf("Tăng cơ / Tăng cân", "Giảm cân", "Duy trì cân nặng", "Tăng cơ", "Tăng cân", "Duy trì")
        
        val meals = listOf(
            // --- BUỔI CHIỀU (CẬP NHẬT TỪ BUỔI TỐI SANG CHIỀU) ---
            mapOf("id" to 4, "name" to "Chuối Chín", "category" to "Rau củ", "time" to "Buổi chiều", "imageRes" to "chuoi"),
            mapOf("id" to 5, "name" to "Trứng Luộc", "category" to "Cơm", "time" to "Buổi chiều", "imageRes" to "trung"),
            mapOf("id" to 6, "name" to "Nước Dừa", "category" to "Nước uống", "time" to "Buổi chiều", "imageRes" to "nuocdua"),

            // --- BUỔI TỐI ---
            mapOf("id" to 1, "name" to "Phở Bò", "category" to "Món nước", "time" to "Buổi tối", "imageRes" to "phobo"),
            mapOf("id" to 13, "name" to "Ức Gà Bông Cải", "category" to "Rau củ", "time" to "Buổi tối", "imageRes" to "uc_ga_bong_cai_xanh"),
            mapOf("id" to 14, "name" to "Salad Cá Ngừ", "category" to "Rau củ", "time" to "Buổi tối", "imageRes" to "salad_ca_ngu"),
            mapOf("id" to 19, "name" to "Nước Lọc", "category" to "Nước uống", "time" to "Buổi tối", "imageRes" to "nuocloc"),

            // --- BUỔI TRƯA ---
            mapOf("id" to 2, "name" to "Bún Mọc", "category" to "Món nước", "time" to "Buổi trưa", "imageRes" to "bunmoc"),
            mapOf("id" to 3, "name" to "Khổ Qua Xào", "category" to "Rau củ", "time" to "Buổi trưa", "imageRes" to "khoqua"),
            mapOf("id" to 10, "name" to "Bò Bít Tết", "category" to "Cơm", "time" to "Buổi trưa", "imageRes" to "bo_bit_tet"),
            mapOf("id" to 11, "name" to "Cơm Tấm Sườn", "category" to "Cơm", "time" to "Buổi trưa", "imageRes" to "comtamsuon"),
            mapOf("id" to 12, "name" to "Miến Xào Hải Sản", "category" to "Cơm", "time" to "Buổi trưa", "imageRes" to "mien_xao_hai_san"),

            // --- BUỔI SÁNG ---
            mapOf("id" to 7, "name" to "Yến Mạch Sữa Tươi", "category" to "Sinh tố", "time" to "Buổi sáng", "imageRes" to "yenmachsuatuoi"),
            mapOf("id" to 8, "name" to "Trứng và Bánh Mì", "category" to "Cơm", "time" to "Buổi sáng", "imageRes" to "trungvabanhmi"),
            mapOf("id" to 9, "name" to "Súp Bí Đỏ", "category" to "Món nước", "time" to "Buổi sáng", "imageRes" to "sup_bi_do")
        ).map { it + mapOf(
            "suitableGoals" to allGoals, 
            "calories" to 300, 
            "description" to "Bữa ăn nhẹ lành mạnh cho ${it["time"]}.", 
            "instructions" to "Chế biến đơn giản.", 
            "time" to (it["time"] ?: "Cả ngày"), 
            "difficulty" to "Dễ", 
            "protein" to 15, "carbs" to 35, "fat" to 8
        )}
        
        val batch = db.batch()
        for (m in meals) batch.set(col.document(m["id"].toString()), m)
        batch.commit().await()
    }

    private suspend fun seedExercises() {
        val col = db.collection("exercises")
        val allTargets = listOf("Tăng cơ / Tăng cân", "Giảm cân", "Duy trì cân nặng", "Tăng cơ", "Tăng cân", "Giảm cân", "Duy trì", "Ngực", "Lưng", "Chân", "Bụng", "Vai", "Toàn thân")
        val exercises = listOf(
            mapOf("name" to "Chùng chân", "videoUrl" to "lunges", "imageUrl" to "chungchan", "muscleGroup" to "Chân"),
            mapOf("name" to "Cử tạ", "videoUrl" to "deadlift", "imageUrl" to "deadlift", "muscleGroup" to "Lưng"),
            mapOf("name" to "Hít xà", "videoUrl" to "pull_up", "imageUrl" to "pullup", "muscleGroup" to "Lưng"),
            mapOf("name" to "Hít đất", "videoUrl" to "push_up", "imageUrl" to "hitdat", "muscleGroup" to "Ngực"),
            mapOf("name" to "Nâng tạ ngực", "videoUrl" to "dumbbell_bench_press", "imageUrl" to "bench", "muscleGroup" to "Ngực"),
            mapOf("name" to "Đẩy tạ đòn", "videoUrl" to "dumbbell_bench_press", "imageUrl" to "daytadon", "muscleGroup" to "Ngực"),
            mapOf("name" to "Nhảy dây", "videoUrl" to "jumping_jacks", "imageUrl" to "nhayday", "muscleGroup" to "Toàn thân"),
            mapOf("name" to "Plank", "videoUrl" to "plank", "imageUrl" to "plank", "muscleGroup" to "Bụng"),
            mapOf("name" to "Squat", "videoUrl" to "barbell_squat", "imageUrl" to "squat", "muscleGroup" to "Chân"),
            mapOf("name" to "Nâng tạ ngang", "videoUrl" to "lateral_raises", "imageUrl" to "vai_core", "muscleGroup" to "Vai")
        ).map { it + mapOf("targets" to allTargets, "caloriesBurned" to 150, "difficulty" to "Trung bình", "reps" to "3 hiệp", "description" to "Bài tập hiệu quả.") }

        val batch = db.batch()
        for (ex in exercises) batch.set(col.document(ex["name"].toString()), ex)
        batch.commit().await()
    }
}
