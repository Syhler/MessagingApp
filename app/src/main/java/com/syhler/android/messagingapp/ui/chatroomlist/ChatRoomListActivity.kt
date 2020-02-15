package com.syhler.android.messagingapp.ui.chatroomlist

import android.content.Intent
import android.graphics.Bitmap
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
import com.syhler.android.messagingapp.ui.chatroomlist.chatroom.ChatRoomActivity
import com.syhler.android.messagingapp.viewmodels.ChatRoomListViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class ChatRoomListActivity : AppCompatActivity(), View.OnClickListener{

    private lateinit var  googleAuth : GoogleAuth
    private val facebookAuth: FacebookAuth = FacebookAuth()
    private var currentUser : CurrentUser = CurrentUser()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_chat_room_list)

        googleAuth = GoogleAuth(this, getString(R.string.default_web_client_id))

        val chatRoomListViewModel = ViewModelProviders.of(this)
            .get(ChatRoomListViewModel::class.java)


        //take a look at this later TODO(LOOOK)
        chatRoomListViewModel.getChatRooms2().observe(this, Observer {

            val chatRoomAdapter = ChatRoomListAdapter(this, it)
            val listView = findViewById<ListView>(R.id.list_chat)
            listView.adapter = chatRoomAdapter

            (listView.adapter as BaseAdapter).notifyDataSetChanged()


            listView.setOnItemClickListener{adapterView, view, position: Int, id: Long ->
                run {

                    val intent = Intent(this, ChatRoomActivity::class.java)
                    if (it.size > position)
                    {
                        intent.putExtra("key", it[position].key)
                    }
                    startActivity(intent)
                }
            }

        })

        currentUser.getProfileImage().invokeOnCompletion {
            updateImage(currentUser.image)
        }

        //button_log_out.setOnClickListener(this)

    }

    private fun updateImage(bitmap: Bitmap)
    {
        GlobalScope.launch(Dispatchers.Main) {
            //profile_pic.setImageBitmap(bitmap)
        }
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
