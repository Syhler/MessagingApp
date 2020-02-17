package com.syhler.android.messagingapp.ui.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatDialogFragment
import com.google.firebase.messaging.FirebaseMessaging

class AskForNotificationDialog(private val chatRoomKey : String) : AppCompatDialogFragment()
{
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        //val chatRoomName = activity?.intent?.getStringExtra(KeyFields.chatRoomName)
        val alertBuilder = AlertDialog.Builder(activity)

        alertBuilder.setTitle("Notification")
            .setMessage("Do you want to enable notification for this room?")
            .setNegativeButton("No") { _, _ ->
                FirebaseMessaging.getInstance().unsubscribeFromTopic(chatRoomKey)
            }
            .setPositiveButton("Yes") {_,_ ->
                FirebaseMessaging.getInstance().subscribeToTopic(chatRoomKey)
            }



        return alertBuilder.create()

    }



}