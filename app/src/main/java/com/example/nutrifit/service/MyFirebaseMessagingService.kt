package com.example.nutrifit.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.nutrifit.MainActivity
import com.example.nutrifit.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Refreshed token: $token")
        sendRegistrationToServer(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Xử lý "Data" message (luôn chạy, kể cả khi app tắt)
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Data Message Payload: " + remoteMessage.data)
            val title = remoteMessage.data["title"]
            val body = remoteMessage.data["body"]
            sendNotification(title, body)
        }
        // Xử lý "Notification" message (chỉ chạy khi app đang mở)
        else {
            remoteMessage.notification?.let {
                Log.d(TAG, "Notification Message Body: ${it.body}")
                sendNotification(it.title, it.body)
            }
        }
    }

    private fun sendRegistrationToServer(token: String?) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null && token != null) {
            val userDocRef = FirebaseFirestore.getInstance().collection("users").document(userId)
            userDocRef.update("fcmToken", token)
                .addOnSuccessListener { Log.d(TAG, "FCM token updated successfully") }
                .addOnFailureListener { e -> Log.w(TAG, "Error updating FCM token", e) }
        }
    }

    private fun sendNotification(title: String?, messageBody: String?) {
        // --- BƯỚC 1: TẠO INTENT ĐỂ MỞ APP ---
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)

        val channelId = "default_channel_id"
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true) // Tự động xóa thông báo khi người dùng nhấn vào
            .setContentIntent(pendingIntent) // --- BƯỚC 2: GÁN PENDINGINTENT VÀO THÔNG BÁO ---

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Thông báo chung",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0, notificationBuilder.build())
    }

    companion object {
        private const val TAG = "MyFirebaseMsgService"
    }
}
