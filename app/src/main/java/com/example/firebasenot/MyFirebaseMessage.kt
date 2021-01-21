package com.example.firebasenot

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessage : FirebaseMessagingService() {

    override fun onMessageReceived(notification: RemoteMessage) {
        notification.notification?.let {
            createNotificationChannel()

            val builder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(it.title)
                .setContentText(it.body)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            with(NotificationManagerCompat.from(this)) {
                // notificationId is a unique int for each notification that you must define
                notify(1, builder.build())
            }
        }
    }

    override fun onNewToken(token: String) {
        Log.i("token", token)
        super.onNewToken(token)
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = CHANNEL_NAME
            val descriptionText = CHANNEL_DESCRIPTION
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        private const val CHANNEL_ID = "Firebase"
        private const val CHANNEL_NAME = "Firebase"
        private const val CHANNEL_DESCRIPTION = "Melhor serviço de todos os tempos"
    }
}