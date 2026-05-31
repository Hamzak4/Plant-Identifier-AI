package com.example.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters

class ReminderWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val plantName = inputData.getString("plant_name") ?: "Your Plant"
        val careType = inputData.getString("care_type") ?: "Watering"

        try {
            showNotification(plantName, careType)
        } catch (e: Exception) {
            android.util.Log.e("ReminderWorker", "Failed to trigger care notification: ${e.message}")
        }
        return Result.success()
    }

    private fun showNotification(plantName: String, careType: String) {
        val context = applicationContext
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channelId = "plant_care_reminders"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Plant Care Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifies about pending plant water/fertilizer activities"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Plant Care Alert: $plantName")
            .setContentText("Your $plantName needs its scheduled '$careType' today! 🌱 Caring is carrying.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}
