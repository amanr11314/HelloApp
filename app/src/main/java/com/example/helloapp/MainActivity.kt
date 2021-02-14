package com.example.helloapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.widget.Button

import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat


class MainActivity : AppCompatActivity() {
    /**
     * Action buttons to notify, cancel and update notification
     *
     */
    lateinit var button_notify: Button
    lateinit var button_cancel: Button
    lateinit var button_update: Button


    private val mReceiver = NotificationReceiver()

    private var mNotifyManager: NotificationManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        createNotificationChannel()

        //register your broadcast receiver
        registerReceiver(mReceiver, IntentFilter(ACTION_UPDATE_NOTIFICATION))

        button_notify = findViewById(R.id.notify)
        button_notify.setOnClickListener {
            sendNotification()
        }

        button_update = findViewById(R.id.update)
        button_update.setOnClickListener {
            updateNotification()
        }

        button_cancel = findViewById(R.id.cancel)
        button_cancel.setOnClickListener {
            cancelNotification()
        }



        setNotificationButtonState(true, false, false)
    }

    override fun onDestroy() {
        //unregister broadcast receiver
        unregisterReceiver(mReceiver)
        super.onDestroy()
    }


    private fun sendNotification() {
        val updateIntent = Intent(ACTION_UPDATE_NOTIFICATION)
        val updatePendingIntent = PendingIntent.getBroadcast(
            this,
            NOTIFICATION_ID,
            updateIntent,
            PendingIntent.FLAG_ONE_SHOT
        )
        //get notification from notification builder
        val notifyBuilder = getNotificationBuilder()
        //add action button as Update notification
        notifyBuilder.addAction(R.drawable.ic_update, "Update Notification", updatePendingIntent)
        //finally build notification using notification manager
        mNotifyManager?.notify(NOTIFICATION_ID, notifyBuilder.build())

        setNotificationButtonState(false, true, true)
    }

    private fun updateNotification() {
        //create new updated notification with style as BigPictureStyle
        val androidImage = BitmapFactory.decodeResource(resources, R.drawable.mascot_1)
        val notifyBuilder = getNotificationBuilder()
        notifyBuilder.setStyle(
            NotificationCompat.BigPictureStyle()
                .bigPicture(androidImage)
                .setBigContentTitle("Notification Updated!")
        )

        mNotifyManager?.notify(NOTIFICATION_ID, notifyBuilder.build())

        setNotificationButtonState(false, false, true)

    }

    private fun cancelNotification() {
        //cancel notification
        mNotifyManager?.cancel(NOTIFICATION_ID)
        setNotificationButtonState(true, false, false)
    }


    private fun createNotificationChannel() {
        mNotifyManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (android.os.Build.VERSION.SDK_INT >=
            android.os.Build.VERSION_CODES.O
        ) {
            // Create a NotificationChannel
            val notificationChannel = NotificationChannel(
                PRIMARY_CHANNEL_ID,
                "Mascot Notification", NotificationManager.IMPORTANCE_HIGH
            )

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = "Notification from Mascot"
            mNotifyManager?.createNotificationChannel(notificationChannel)
        }
    }


    private fun getNotificationBuilder(): NotificationCompat.Builder {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val notificationPendingIntent = PendingIntent.getActivity(
            this,
            NOTIFICATION_ID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        //build initial notification
        val notifyBuilder = NotificationCompat.Builder(this, PRIMARY_CHANNEL_ID)
            .setContentTitle("You've been notified!")
            .setContentText("This is your notification text.")
            .setSmallIcon(R.drawable.ic_android)
            .setAutoCancel(true)
            .setContentIntent(notificationPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)

        return notifyBuilder
    }

    private fun setNotificationButtonState(
        isNotifyEnabled: Boolean?,
        isUpdateEnabled: Boolean?,
        isCancelEnabled: Boolean?
    ) {
        button_notify.isEnabled = isNotifyEnabled!!
        button_update.isEnabled = isUpdateEnabled!!
        button_cancel.isEnabled = isCancelEnabled!!
    }

    /**
     * Broadcast receiver to update notification
     */
    inner class NotificationReceiver() : BroadcastReceiver() {
        override fun onReceive(
            context: Context,
            intent: Intent
        ) {
            // Update the notification
            updateNotification()
        }
    }

    companion object {
        private const val PRIMARY_CHANNEL_ID = "primary_notification_channel"
        private const val ACTION_UPDATE_NOTIFICATION =
            "com.example.helloapp.ACTION_UPDATE_NOTIFICATION"
        private const val NOTIFICATION_ID = 0
    }
}