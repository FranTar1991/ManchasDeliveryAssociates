package com.manchasdelivery_associates.main_fragment

import android.app.*
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.manchasdelivery_associates.R
import com.manchasdelivery_associates.main_activity.MainActivity
import com.manchasdelivery_associates.utils.sendRegistrationToServer
import java.security.SecureRandom

class MessagingService() : FirebaseMessagingService(){

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        if (remoteMessage.data.isNotEmpty()) {
            val serverId = remoteMessage.data["serverId"]
            if (serverId == FirebaseAuth.getInstance().currentUser?.uid){
                sendNotification("Vamos a trabajar" )
            }
        }

        // Check if message contains a notification payload.
        remoteMessage.notification?.let {

        }

    }

    override fun onNewToken(token: String) {

        FirebaseAuth.getInstance().currentUser?.uid?.let {
            sendRegistrationToServer(token, it)
        }
    }


    private fun sendNotification(messageBody: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(this, createRandomCode(3), intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)

        val channelId = getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(getString(R.string.fcm_message))
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
        notificationBuilder.setDefaults(Notification.DEFAULT_SOUND or Notification.DEFAULT_LIGHTS or Notification.DEFAULT_VIBRATE)


        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId,
                "ManMan Notifications",
                NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = notificationBuilder.build()


        notificationManager.notify(createRandomCode(3), notification)
    }

    fun createRandomCode(codeLength: Int): Int {
        val chars = "1234567890".toCharArray()
        val sb = StringBuilder()
        val random = SecureRandom()
        for (i in 0 until codeLength) {
            val c = chars[random.nextInt(chars.size)]
            sb.append(c)
        }
        return sb.toString().toInt()
    }

    companion object {

        private const val TAG = "My_FirebaseMsgService"
    }


}