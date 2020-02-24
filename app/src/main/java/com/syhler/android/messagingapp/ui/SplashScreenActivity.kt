package com.syhler.android.messagingapp.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.syhler.android.messagingapp.authenticate.CurrentUser
import com.syhler.android.messagingapp.ui.chatroomlist.ChatRoomListActivity


class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (CurrentUser.initialize(this).isloggedIn)
        {
            startActivity(Intent(this, ChatRoomListActivity::class.java))
        }
        else
        {
            startActivity(Intent(this, LoginActivity::class.java))
        }

    }
}
