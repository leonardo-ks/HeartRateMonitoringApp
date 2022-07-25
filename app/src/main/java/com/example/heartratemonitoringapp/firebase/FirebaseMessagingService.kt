package com.example.heartratemonitoringapp.firebase

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.IconCompat
import com.example.heartratemonitoringapp.R
import com.example.heartratemonitoringapp.TestActivity
import com.example.heartratemonitoringapp.form.FormActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


private const val CHANNEL_ID = "fcm_channel"

class FirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)

    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationId = 100

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(message.data["title"])
            .setContentText(message.data["message"])
            .setSmallIcon(R.drawable.ic_watch)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()


        val fullScreenIntent = Intent(this, FormActivity::class.java)
        val fullScreenPendingIntent = PendingIntent.getActivity(this, 0,
            fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val action = NotificationCompat.Action.Builder(
            IconCompat.createWithResource(applicationContext, R.drawable.ic_watch),
                "terima",
                fullScreenPendingIntent
        ).build()

        val intent = Intent(this, TestActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setDefaults(Notification.DEFAULT_ALL)
            .setContentTitle(message.data["title"])
            .setContentText(message.data["message"])
            .setSmallIcon(R.drawable.ic_watch)
            .setOngoing(true)
//            .setCustomContentView(RemoteViews(packageName, R.layout.activity_test))
            .setFullScreenIntent(pendingIntent, true)

        val incomingCallNotification = notificationBuilder.build()

        Log.d("vibrate", (message.data["vibrate"] == "true").toString())

        if (message.data["status"].equals("5")) {
            notificationManager.notify(notificationId, incomingCallNotification)
        } else {
            notificationManager.notify(notificationId, notification)
        }


        if (message.data["vibrate"] == "true") {
            vibrate()
        }

    }

    private fun vibrate() {
        val vib = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(VIBRATOR_SERVICE) as Vibrator
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vib.vibrate(VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE) )
        }else{
            @Suppress("DEPRECATION")
            vib.vibrate(300)
        }
    }

    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channelName = "channel"
        val channel = NotificationChannel(CHANNEL_ID, channelName, IMPORTANCE_HIGH)
        notificationManager.createNotificationChannel(channel)
    }
}