package com.manchasdelivery_associates.main_fragment

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.manchasdelivery_associates.utils.sendRegistrationToServer

class MessagingService() : FirebaseMessagingService(){

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        if (remoteMessage.data.isNotEmpty()) {
            Log.i("MyMessageNotification", "Message data Body: ${remoteMessage.data}")
        }

        // Check if message contains a notification payload.
        remoteMessage.notification?.let {
            Log.i("MyMessageNotification", "Message Notification Body: ${it.body}")
        }

    }

    override fun onNewToken(token: String) {

        FirebaseAuth.getInstance().currentUser?.uid?.let {
            sendRegistrationToServer(token, it)
        }
    }


}