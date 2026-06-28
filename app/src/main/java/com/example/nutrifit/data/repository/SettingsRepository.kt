package com.example.nutrifit.data.repository

import android.app.Application
import android.content.Context
import com.example.nutrifit.MainApplication

class SettingsRepository(private val application: Application) {

    private val sharedPreferences = application.getSharedPreferences("settings_prefs", Context.MODE_PRIVATE)

    fun getSettings(): Map<String, Any> {
        val language = sharedPreferences.getString("language", "Tiếng Việt") ?: "Tiếng Việt"
        val units = sharedPreferences.getString("units", "kg / cm") ?: "kg / cm"
        val notifications = sharedPreferences.getBoolean("notifications", false)
        val twoFactor = sharedPreferences.getBoolean("twoFactor", false)
        return mapOf(
            "language" to language,
            "units" to units,
            "notifications" to notifications,
            "twoFactor" to twoFactor
        )
    }

    fun saveSettings(language: String, units: String, notifications: Boolean, twoFactor: Boolean) {
        with(sharedPreferences.edit()) {
            putString("language", language)
            putString("units", units)
            putBoolean("notifications", notifications)
            putBoolean("twoFactor", twoFactor)
            apply()
        }

        // Thêm logic WorkManager vào đây
        if (notifications) {
            MainApplication.scheduleAllDailyReminders(application.applicationContext)
        } else {
            MainApplication.cancelAllDailyReminders(application.applicationContext)
        }
    }
}