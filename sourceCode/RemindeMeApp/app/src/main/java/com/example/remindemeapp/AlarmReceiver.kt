package com.example.remindemeapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.BroadcastReceiver
import android.content.Context
import android.util.Log
import androidx.core.content.ContextCompat.startActivity
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService

class AlarmReceiver : BroadcastReceiver() {
    val CHANNEL_ID = "reminderID"
    val CHANNEL_NAME = "reminders"
    val NOTIFICATION_ID = 0


    override fun onReceive(context: Context, intent: Intent?) {
        val message = intent?.getStringExtra("EXTRA_MESSAGE") ?: return
        println("Alarm triggered: $message")

        //var intent = Intent(context, MainActivity::class.java)
        //val pendingIntent = TaskStackBuilder.create(context).run {
            //addNextIntentWithParentStack(intent)
            //getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        //}

        createNotification(context, message)
    }

    fun createNotification(context: Context, notificationText: String) {
        //Notification
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Reminder")
            .setContentText(notificationText)
            .setSmallIcon(R.drawable.ic_notification)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            //.setContentIntent(pendingIntent)
            .build()
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}