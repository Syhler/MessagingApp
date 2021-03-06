package com.syhler.android.messagingapp.notification

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.syhler.android.messagingapp.R
import com.syhler.android.messagingapp.authenticate.CurrentUser
import com.syhler.android.messagingapp.ui.chatroom.ChatRoomActivity
import com.syhler.android.messagingapp.utillities.KeyFields
import java.util.*


class NotificationService : FirebaseMessagingService() {

    private val NEW_MESSAGE_CHANNEL = "new_message_channel"

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val messageFrom = remoteMessage.data[KeyFields.currentUserAuth]


        //makes sure you dont receives your own notifications
        if(!CurrentUser.getInstance().isloggedIn || CurrentUser.getInstance().authenticationID == messageFrom) {
            return
        }

        val chatRoomKey = remoteMessage.data[KeyFields.chatRoomKey]

        //makes sure you dont receives any messages if you already in the room the messages are comming from
        if (chatRoomKey != null && chatRoomKey == CurrentUser.getInstance().currentChatRoomKey) {
            return
        }



        val intent = Intent(this, ChatRoomActivity::class.java)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationID = Random().nextInt(3000)

        val chatRoomName = remoteMessage.data[KeyFields.chatRoomName]

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
            R.drawable.baseline_chat_black_24dp
        )


        val notificationSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, NEW_MESSAGE_CHANNEL)
            .setSmallIcon(R.drawable.app_icon_without_text_transparent)
            .setLargeIcon(largeIcon)
            .setContentTitle(remoteMessage.data["title"])
            .setAutoCancel(true)
            .setSound(notificationSoundUri)
            .setContentIntent(pendingIntent)

        if (remoteMessage.data["message"]?.isBlank()!!)
        {
            notificationBuilder.setContentText("Sent image")
        }
        else
        {
            notificationBuilder.setContentText(remoteMessage.data["message"])
        }



        //Set notification color to match your app color template
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.color = ContextCompat.getColor(this, R.color.colorPrimaryDark)
        }



        notificationManager.notify(notificationID, notificationBuilder.build())
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun setupChannels(notificationManager: NotificationManager?) {
        val channelName = "New notification"
        val channelDescription = "Chat room notification"

        val channel: NotificationChannel
        channel = NotificationChannel(NEW_MESSAGE_CHANNEL, channelName, NotificationManager.IMPORTANCE_HIGH)
        channel.description = channelDescription
        channel.enableLights(true)
        channel.lightColor = Color.GREEN
        channel.enableVibration(true)
        notificationManager?.createNotificationChannel(channel)
    }

}
