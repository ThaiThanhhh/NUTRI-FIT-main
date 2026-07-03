package com.example.nutrifit.utils

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
                // 1. Dọn sạch dữ liệu cũ
                clearCollection("meals")
                clearCollection("exercises")

                // 2. Nạp dữ liệu mới chuẩn 100% theo yêu cầu
                seedMeals()
                seedExercises()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private suspend fun clearCollection(path: String) {
        val snapshot = db.collection(path).get().await()
        val batch = db.batch()
        for (doc in snapshot.documents) batch.delete(doc.reference)
        batch.commit().await()
    }

    private fun seedMeals() {
        val col = db.collection("meals")
        val allGoals = listOf("Tăng cơ / Tăng cân", "Giảm cân", "Duy trì cân nặng", "Tăng cơ", "Tăng cân", "Duy trì")
        
        val meals = listOf(
            mapOf("id" to 1, "name" to "Bò Bít Tết", "category" to "Cơm", "imageRes" to "bo_bit_tet"),
            mapOf("id" to 2, "name" to "Miến Xào Hải Sản", "category" to "Món nước", "imageRes" to "mien_xao_hai_san"),
            mapOf("id" to 3, "name" to "Ức Gà Bông Cải", "category" to "Rau củ", "imageRes" to "uc_ga_bong_cai_xanh"),
            mapOf("id" to 4, "name" to "Súp Bí Đỏ", "category" to "Món nước", "imageRes" to "sup_bi_do"),
            mapOf("id" to 5, "name" to "Sinh Tố Bơ", "category" to "Sinh tố", "imageRes" to "stbo"),
            mapOf("id" to 6, "name" to "Yến Mạch Sữa Tươi", "category" to "Sinh tố", "imageRes" to "yenmachsuatuoi"),
            mapOf("id" to 7, "name" to "Salad Cá Ngừ", "category" to "Rau củ", "imageRes" to "salad_ca_ngu"),
            mapOf("id" to 8, "name" to "Salad Ức Gà", "category" to "Rau củ", "imageRes" to "salad_uc_ga"),
            mapOf("id" to 9, "name" to "Khoai Lang Ức Gà", "category" to "Rau củ", "imageRes" to "khoailangucga"),
            mapOf("id" to 10, "name" to "Sinh Tố Chuối", "category" to "Sinh tố", "imageRes" to "sinh_to_chuoi"),
            mapOf("id" to 11, "name" to "Rau Củ Luộc", "category" to "Rau củ", "imageRes" to "rau_cu_luoc"),
            mapOf("id" to 12, "name" to "Nước Ép Cà Rốt", "category" to "Nước uống", "imageRes" to "nuoc_ep_ca_rot"),
            mapOf("id" to 13, "name" to "Sữa Chua Trái Cây", "category" to "Nước uống", "imageRes" to "suachuatraicay"),
            mapOf("id" to 14, "name" to "Trứng và Bánh Mì", "category" to "Cơm", "imageRes" to "trungvabanhmi"),
            mapOf("id" to 15, "name" to "Sinh Tố Dâu", "category" to "Sinh tố", "imageRes" to "stdau"),
            mapOf("id" to 16, "name" to "Sinh Tố Táo", "category" to "Sinh tố", "imageRes" to "sttao"),
            mapOf("id" to 17, "name" to "Sinh Tố Xoài", "category" to "Sinh tố", "imageRes" to "stxoai"),
            mapOf("id" to 18, "name" to "Yến Mạch Hoa Quả", "category" to "Sinh tố", "imageRes" to "yen_mach_hoa_qua")
        ).map { it + mapOf(
            "suitableGoals" to allGoals,
            "calories" to 350,
            "description" to "Bữa ăn giàu dinh dưỡng hỗ trợ sức khỏe.",
            "instructions" to "1. Chuẩn bị nguyên liệu.\n2. Chế biến nhanh.\n3. Thưởng thức.",
            "time" to "15 phút",
            "difficulty" to "Dễ",
            "protein" to 20, "carbs" to 40, "fat" to 10
        )}

        val batch = db.batch()
        for (m in meals) {
            val id = m["id"].toString()
            batch.set(col.document(id), m)
        }
        batch.commit()
    }

    private fun seedExercises() {
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
        ).map { it + mapOf(
            "targets" to allTargets,
            "caloriesBurned" to 120,
            "difficulty" to "Trung bình",
            "reps" to "3 hiệp x 12 lần",
            "description" to "Bài tập giúp rèn luyện thể lực và vóc dáng."
        )}

        val batch = db.batch()
        for (ex in exercises) {
            val name = ex["name"].toString()
            batch.set(col.document(name), ex)
        }
        batch.commit()
    }
}
