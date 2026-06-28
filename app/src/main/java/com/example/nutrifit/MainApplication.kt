package com.example.nutrifit

import android.app.Application
import android.content.Context
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.nutrifit.worker.NotificationWorker
import com.jakewharton.threetenabp.AndroidThreeTen
import java.util.Calendar
import java.util.concurrent.TimeUnit

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
    }

    companion object {
        private const val BREAKFAST_WORK_TAG = "breakfast_notification"
        private const val LUNCH_WORK_TAG = "lunch_notification"
        private const val WORKOUT_WORK_TAG = "workout_notification"
        private const val DINNER_WORK_TAG = "dinner_notification"

        fun scheduleAllDailyReminders(context: Context) {
            scheduleReminder(context, 7, 0, BREAKFAST_WORK_TAG, "Chào buổi sáng!", "Đừng quên một bữa sáng đủ chất nhé.")
            scheduleReminder(context, 12, 0, LUNCH_WORK_TAG, "Tới giờ ăn trưa rồi", "Nạp năng lượng cho buổi chiều thôi!")
            scheduleReminder(context, 16, 0, WORKOUT_WORK_TAG, "Giờ tập luyện tới rồi!", "Hãy hoàn thành bài tập hôm nay nhé.")
            scheduleReminder(context, 19, 0, DINNER_WORK_TAG, "Ăn tối thôi nào!", "Một bữa tối nhẹ nhàng sẽ giúp bạn ngủ ngon hơn.")
        }

        fun cancelAllDailyReminders(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(BREAKFAST_WORK_TAG)
            WorkManager.getInstance(context).cancelUniqueWork(LUNCH_WORK_TAG)
            WorkManager.getInstance(context).cancelUniqueWork(WORKOUT_WORK_TAG)
            WorkManager.getInstance(context).cancelUniqueWork(DINNER_WORK_TAG)
        }

        fun scheduleTestNotification(context: Context) {
            val workData = Data.Builder()
                .putString("title", "Thông báo thử nghiệm")
                .putString("message", "Thông báo này sẽ xuất hiện sau 1 phút.")
                .build()

            val testRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
                .setInputData(workData)
                .setInitialDelay(1, TimeUnit.MINUTES)
                .build()
            
            WorkManager.getInstance(context).enqueue(testRequest)
        }

        private fun scheduleReminder(context: Context, hour: Int, minute: Int, tag: String, title: String, message: String) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .build()

            val workData = Data.Builder()
                .putString("title", title)
                .putString("message", message)
                .build()

            val workRequest = PeriodicWorkRequestBuilder<NotificationWorker>(1, TimeUnit.DAYS)
                .setConstraints(constraints)
                .setInputData(workData)
                .setInitialDelay(calculateInitialDelay(hour, minute), TimeUnit.MILLISECONDS)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                tag,
                ExistingPeriodicWorkPolicy.REPLACE,
                workRequest
            )
        }

        private fun calculateInitialDelay(hour: Int, minute: Int): Long {
            val currentTime = Calendar.getInstance()
            val targetTime = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                if (before(currentTime)) {
                    add(Calendar.DAY_OF_MONTH, 1)
                }
            }
            return targetTime.timeInMillis - currentTime.timeInMillis
        }
    }
}