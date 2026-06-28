package com.example.nutrifit.data.repository

import com.example.nutrifit.data.model.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRepository {

    private val db = FirebaseFirestore.getInstance()

    suspend fun saveUser(user: User): Result<Unit> {
        return try {
            db.collection("users").document(user.id).set(user).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUser(userId: String): Result<User> {
        return try {
            val document = db.collection("users").document(userId).get().await()
            if (document.exists()) {
                val user = document.toObject(User::class.java)
                if (user != null) {
                    Result.success(user)
                } else {
                    Result.failure(Exception("Failed to parse user data."))
                }
            } else {
                Result.failure(Exception("User not found."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}