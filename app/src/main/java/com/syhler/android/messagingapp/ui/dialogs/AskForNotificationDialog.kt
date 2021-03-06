package com.syhler.android.messagingapp.ui.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatDialogFragment
import com.google.firebase.messaging.FirebaseMessaging
import com.syhler.android.messagingapp.authenticate.CurrentUser

class AskForNotificationDialog(private val chatRoomKey : String) : AppCompatDialogFragment()
{
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val alertBuilder = AlertDialog.Builder(this.activity)

        alertBuilder.setTitle("Notification")
            .setMessage("Do you want to enable notification for this room?")
            .setNegativeButton("No") { _, _ ->
                CurrentUser.getInstance().addNotificationStatus(false, chatRoomKey)
                FirebaseMessaging.getInstance().unsubscribeFromTopic(chatRoomKey)
            }
            .setPositiveButton("Yes") {_,_ ->
                CurrentUser.getInstance().addNotificationStatus(true, chatRoomKey)
                FirebaseMessaging.getInstance().subscribeToTopic(chatRoomKey)
            }



        return alertBuilder.create()

    }



}