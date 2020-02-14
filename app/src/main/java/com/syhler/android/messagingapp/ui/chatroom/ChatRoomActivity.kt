package com.syhler.android.messagingapp.ui.chatroom

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.syhler.android.messagingapp.MainActivity
import com.syhler.android.messagingapp.R
import com.syhler.android.messagingapp.authenticate.CurrentUser
import com.syhler.android.messagingapp.authenticate.FacebookAuth
import com.syhler.android.messagingapp.authenticate.GoogleAuth
import com.syhler.android.messagingapp.authenticate.enums.AuthenticationMethod
import com.syhler.android.messagingapp.data.Database
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class ChatRoomActivity : AppCompatActivity(), View.OnClickListener{

    private lateinit var  googleAuth : GoogleAuth
    private val facebookAuth: FacebookAuth = FacebookAuth()
    private var currentUser : CurrentUser = CurrentUser()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_chat_room)

        googleAuth = GoogleAuth(this, getString(R.string.default_web_client_id))

        Database.chatRooms.observe(this, Observer {

            val chatRoomAdapter = ChatRoomAdapter(this, Database.chatRooms)
            val listView = findViewById<ListView>(R.id.list_chat)
            listView.adapter = chatRoomAdapter

            (listView.adapter as BaseAdapter).notifyDataSetChanged()


            listView.setOnItemClickListener{adapterView, view, position: Int, id: Long ->
                run {
                    Toast.makeText(this, "You clicked at : $position", Toast.LENGTH_LONG).show()
                }
            }

        })










        currentUser.getProfileImage().invokeOnCompletion {
            updateImage(currentUser.image)
        }

        button_log_out.setOnClickListener(this)

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
