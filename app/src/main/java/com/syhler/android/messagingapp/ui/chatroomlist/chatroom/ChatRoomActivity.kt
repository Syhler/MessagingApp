package com.syhler.android.messagingapp.ui.chatroomlist.chatroom

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.widget.ImageButton
import android.widget.ListView
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.syhler.android.messagingapp.R
import com.syhler.android.messagingapp.authenticate.CurrentUser
import com.syhler.android.messagingapp.data.entites.Message
import com.syhler.android.messagingapp.data.entites.User
import com.syhler.android.messagingapp.utillities.Dependencies
import kotlinx.android.synthetic.main.activity_chat_room.*
import java.io.ByteArrayOutputStream

class ChatRoomActivity : AppCompatActivity() {


    private lateinit var viewModel : ChatRoomViewModel
    private lateinit var messageAdapter: MessageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_room)

        val chatRoomKey = intent.getStringExtra("key")
        if (chatRoomKey != null)
        {
            val factory = Dependencies.provideChatRoomViewModelFactory(chatRoomKey)
            viewModel = ViewModelProviders.of(this, factory)
                .get(ChatRoomViewModel::class.java)
        }

        messageAdapter = MessageAdapter(this)

        val listView = findViewById<ListView>(R.id.messages_view)
        listView.adapter = messageAdapter

        viewModel.getInitMessages().observe(this, Observer {
            if (it != null)
            {
                messageAdapter.addAll(it)
            }
        })




        viewModel.latestMessage.observe(this, Observer {
            if (it != null)
            {
                if (it.user.userAuthID != CurrentUser.getInstace().authenticationID)
                {
                    messageAdapter.add(it)
                }
            }
        })




        message_input_field.hint = chatRoomKey

        findViewById<ImageButton>(R.id.message_send_button).setOnClickListener { onMessageSend() }

    }


    fun onMessageSend()
    {
        val inputField = findViewById<TextView>(R.id.message_input_field)

        //make empty check

        val currentUser = CurrentUser.getInstace()
        val user = User(imageToString(currentUser.image), currentUser.fullName!!, currentUser.authenticationID)
        val message = Message(inputField.text.toString(), user, System.currentTimeMillis())
        viewModel.addMessage(message)
        messageAdapter.add(message)
        inputField.setText("")

    }

    private fun imageToString(image : Bitmap) : String
    {
        val byteOutStream = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.PNG, 100, byteOutStream)
        val b: ByteArray = byteOutStream.toByteArray()
        return Base64.encodeToString(b, Base64.DEFAULT)
    }

}
