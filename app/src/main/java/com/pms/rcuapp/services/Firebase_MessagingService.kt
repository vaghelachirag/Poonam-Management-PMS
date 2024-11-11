package com.pms.rcuapp.services

import com.google.firebase.messaging.FirebaseMessagingService
import android.app.Notification
import android.app.NotificationChannel
import android.os.Build
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.graphics.Color
import android.util.Log
import com.google.firebase.messaging.RemoteMessage
import android.content.Intent
import android.graphics.BitmapFactory
import com.squmish.rcuapp.MainActivity
import com.pms.rcuapp.uttils.Session
import com.pms.rcuapp.uttils.Session.Companion.KEY_USER_TOKEN


class Firebase_MessagingService : FirebaseMessagingService() {

    lateinit var notificationManager: NotificationManager
    lateinit var notificationChannel: NotificationChannel
    lateinit var builder: Notification.Builder
    private val channelId = "i.apps.notifications"
    private val description = "Test notification"

    override fun onNewToken(s: String) {
        Log.i("NEW_TOKEN", "******************************")
        Log.i("NEW_TOKEN", "******************************")
        Log.i("NEW_TOKEN", "******************************")
        Log.i("NEW_TOKEN", "******************************")
        Log.i("NEW_TOKEN", "******************************")
        Log.i("NEW_TOKEN", s)
        Log.i("NEW_TOKEN", "******************************")
        Log.i("NEW_TOKEN", "******************************")
        Log.i("NEW_TOKEN", "******************************")
        Log.i("NEW_TOKEN", "******************************")
        Log.i("NEW_TOKEN", "******************************")

        var session = Session(applicationContext)
        session.storeTokenByKey(KEY_USER_TOKEN,s)
        getSharedPreferences("_", MODE_PRIVATE).edit().putString("fb", s).apply()
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d("onMessageReceived", "onMessageReceived: ${remoteMessage.notification!!.body}")


        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


        val intent = Intent(this, MainActivity::class.java)
        var pendingIntent : PendingIntent? = null

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE) ;
        }
        else
        {
            pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }


        // checking if android version is greater than oreo(API 26) or not
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = NotificationChannel(channelId, description, NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.GREEN
            notificationChannel.enableVibration(false)
            notificationManager.createNotificationChannel(notificationChannel)

            builder = Notification.Builder(this, channelId)
                .setContentTitle(remoteMessage.notification!!.title)
                .setContentText(remoteMessage.notification!!.body)
                .setSmallIcon(com.squmish.rcuapp.R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(this.resources, com.squmish.rcuapp.R.drawable.ic_launcher_background))
                .setContentIntent(pendingIntent)
        } else {

            builder = Notification.Builder(this)
                .setContentTitle(remoteMessage.notification!!.title)
                .setContentText(remoteMessage.notification!!.body)
                .setSmallIcon(com.squmish.rcuapp.R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(this.resources, com.squmish.rcuapp.R.drawable.ic_launcher_background))
                .setContentIntent(pendingIntent)
        }
        notificationManager.notify(1234, builder.build())

    }
}