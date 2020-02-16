package com.syhler.android.messagingapp.ui.chatroomlist

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.BaseAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.syhler.android.messagingapp.MainActivity
import com.syhler.android.messagingapp.R
import com.syhler.android.messagingapp.authenticate.CurrentUser
import com.syhler.android.messagingapp.authenticate.FacebookAuth
import com.syhler.android.messagingapp.authenticate.GoogleAuth
import com.syhler.android.messagingapp.authenticate.enums.AuthenticationMethod
import com.syhler.android.messagingapp.data.entites.ChatRoom
import com.syhler.android.messagingapp.ui.chatroomlist.chatroom.ChatRoomActivity
import com.syhler.android.messagingapp.viewmodels.ChatRoomListViewModel


class ChatRoomListActivity : AppCompatActivity(), View.OnClickListener{

    private lateinit var  googleAuth : GoogleAuth
    private val facebookAuth: FacebookAuth = FacebookAuth()
    private var currentUser : CurrentUser = CurrentUser()

    private lateinit var viewModel : ChatRoomListViewModel
    private lateinit var chatRoomAdapter : ChatRoomListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_chat_room_list)

        //TODO(COMBINE google auth and facebook auth into one class for simplier use)
        googleAuth = GoogleAuth(this, getString(R.string.default_web_client_id))

        viewModel = ViewModelProviders.of(this).get(ChatRoomListViewModel::class.java)


        val chatRoomAdapter = ChatRoomListAdapter(this)

        val listView = findViewById<ListView>(R.id.list_chat)
        listView.adapter = chatRoomAdapter

        viewModel.getChatRooms().observe(this, Observer { chatRooms ->

            chatRoomAdapter.addAll(chatRooms)

            listView.setOnItemClickListener{ _, _, position: Int, _: Long ->
                run {
                    changeActivityToChatRoom(chatRooms,position)
                }
            }

        })

        //take a look at this later TODO(LOOOK)


    }

    private fun initListViewChatRooms()
    {

    }

    private fun changeActivityToChatRoom(chatRooms : List<ChatRoom>, position : Int)
    {
        val intent = Intent(this, ChatRoomActivity::class.java)
        if (chatRooms.size > position)
        {
            intent.putExtra("key", chatRooms[position].key) //TODO(create a class that hold all intent values)
        }
        startActivity(intent)
    }

    private fun signOut() {
        when(currentUser.authenticationMethod)
        {
            AuthenticationMethod.FACEBOOK ->
            {
                facebookAuth.signOut()
                changeToLoginScreen()

            }
            AuthenticationMethod.GOOGLE -> {
                googleAuth.signOut()?.addOnCompleteListener {
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
