package com.syhler.android.messagingapp.ui.chatroomlist.chatroom

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.syhler.android.messagingapp.R
import kotlinx.android.synthetic.main.activity_chat_room.*

class ChatRoomActivity : AppCompatActivity() {




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_room)

        val chatRoomKey = intent.getStringExtra("key")

        test_message.text = chatRoomKey

    }
}
