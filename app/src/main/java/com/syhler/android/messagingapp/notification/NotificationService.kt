package com.syhler.android.messagingapp.notification

import android.app.*
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.syhler.android.messagingapp.R
import com.syhler.android.messagingapp.authenticate.CurrentUser
import com.syhler.android.messagingapp.ui.chatroom.ChatRoomActivity
import com.syhler.android.messagingapp.utillities.KeyFields


class NotificationService : FirebaseMessagingService() {

    private val NEW_MESSAGE_CHANNEL = "new_message_channel"

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val chatRoomKey = remoteMessage.data[KeyFields.chatRoomKey]




        if (chatRoomKey != null && chatRoomKey == CurrentUser.getInstance().chatRoomKey)
        {
            return
        }
        /*
        val currentOpenActivity = getCurrentOpenActiviyName()
        if (currentOpenActivity == ChatRoomActivity::class.java.name) {
            return
        }

         */


        val intent = Intent(this, ChatRoomActivity::class.java)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationID = 5 // setting a static id because of multiple chatting

        val chatRoomName = remoteMessage.data[KeyFields.chatRoomName]
        val imageByte = remoteMessage.data[KeyFields.userImage]

        intent.putExtra(KeyFields.chatRoomKey, chatRoomKey)
        intent.putExtra(KeyFields.chatRoomName, chatRoomName)



        /*
        Apps targeting SDK 26 or above (Android O) must implement notification channels and add its notifications
        to at least one of them.
      */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setupChannels(notificationManager)
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT
        )

        val largeIcon = BitmapFactory.decodeResource(
            resources,
            R.drawable.bpfull
        )

        //val largeIcon = BitmapManipulation.getFromByte(imageByte)

        val notificationSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, NEW_MESSAGE_CHANNEL)
            .setSmallIcon(R.drawable.bpfull)
            .setLargeIcon(largeIcon)
            .setContentTitle(remoteMessage.data["title"])
            .setContentText(remoteMessage.data["message"])
            .setAutoCancel(true)
            .setSound(notificationSoundUri)
            .setContentIntent(pendingIntent)

        //Set notification color to match your app color template
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.color = resources.getColor(R.color.colorPrimary)
        }

        notificationManager.notify(notificationID, notificationBuilder.build())
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun setupChannels(notificationManager: NotificationManager?) {
        val adminChannelName = "New notification"
        val adminChannelDescription = "Device to device notification"

        val adminChannel: NotificationChannel
        adminChannel = NotificationChannel(NEW_MESSAGE_CHANNEL, adminChannelName, NotificationManager.IMPORTANCE_HIGH)
        adminChannel.description = adminChannelDescription
        adminChannel.enableLights(true)
        adminChannel.lightColor = Color.RED
        adminChannel.enableVibration(true)
        notificationManager?.createNotificationChannel(adminChannel)
    }

    private fun getCurrentOpenActiviyName() : String
    {
        val cn: ComponentName
        val am = applicationContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

        cn = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            am.appTasks[0].taskInfo.topActivity!!
        } else {
            //tbh i dont know if i should include this or no
            am.getRunningTasks(1)[0].topActivity!! // for systems below lollipop
        }
        am
        return cn.className
    }


}
