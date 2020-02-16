package com.syhler.android.messagingapp.ui.chatroomlist

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.syhler.android.messagingapp.ui.MainActivity
import com.syhler.android.messagingapp.R
import com.syhler.android.messagingapp.authenticate.AuthenticationHandler
import com.syhler.android.messagingapp.authenticate.CurrentUser
import com.syhler.android.messagingapp.authenticate.enums.AuthenticationMethod
import com.syhler.android.messagingapp.data.entites.ChatRoom
import com.syhler.android.messagingapp.ui.chatroom.ChatRoomActivity
import com.syhler.android.messagingapp.ui.chatroomlist.adapter.ChatRoomListAdapter
import com.syhler.android.messagingapp.utillities.Dependencies
import com.syhler.android.messagingapp.utillities.KeyFields
import com.syhler.android.messagingapp.viewmodels.ChatRoomListViewModel


class ChatRoomListActivity : AppCompatActivity(), View.OnClickListener{

    private lateinit var  authenticationHandler : AuthenticationHandler

    private lateinit var viewModel : ChatRoomListViewModel
    private lateinit var chatRoomAdapter : ChatRoomListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_room_list)

        authenticationHandler = AuthenticationHandler(this, getString(R.string.default_web_client_id))

        viewModel = createViewModel()


        chatRoomAdapter =
            ChatRoomListAdapter(this)

        val listView = findViewById<ListView>(R.id.list_chat)
        listView.adapter = chatRoomAdapter

        val button = findViewById<Button>(R.id.button_log_out)
        button.setOnClickListener(this)

        initListViewChatRooms(listView)
    }

    private fun createViewModel() : ChatRoomListViewModel
    {
        val factory = Dependencies.provideChatRoomListViewModelFactory()
        return ViewModelProviders.of(this, factory)
            .get(ChatRoomListViewModel::class.java)
    }

    private fun initListViewChatRooms(listView: ListView)
    {

        viewModel.getChatRooms().observe(this, Observer { chatRooms ->

            chatRoomAdapter.addAll(chatRooms)

            listView.setOnItemClickListener{ _, _, position: Int, _: Long ->
                run {
                    changeActivityToChatRoom(chatRooms,position)
                }
            }

        })
    }

    private fun changeActivityToChatRoom(chatRooms : List<ChatRoom>, position : Int)
    {
        val intent = Intent(this, ChatRoomActivity::class.java)
        if (chatRooms.size > position)
        {
            val chatRoom = chatRooms[position]
            intent.putExtra(KeyFields.chatRoomKey, chatRoom.key) //TODO(create a class that hold all intent values)
            intent.putExtra(KeyFields.chatRoomName, chatRoom.name)
        }
        startActivity(intent)
    }

    private fun signOut() {
        when(CurrentUser.getInstance().authenticationMethod)
        {
            AuthenticationMethod.FACEBOOK ->
            {
                authenticationHandler.facebook.signOut()
                CurrentUser.initialize() // empties the current user data
                changeToLoginScreen()

            }
            AuthenticationMethod.GOOGLE -> {
                authenticationHandler.google.signOut()?.addOnCompleteListener {
                    CurrentUser.initialize() // empties the current user data
                    changeToLoginScreen()
                }
            }
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.button_log_out -> signOut()
        }
    }

    override fun onBackPressed() {
        //Nothing shall happen - shouldn't be allowed to go back to login activity without the use logging off
    }

    private fun changeToLoginScreen()
    {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}
