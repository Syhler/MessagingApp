package com.syhler.android.messagingapp.ui.chatroom

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.syhler.android.messagingapp.MainActivity
import com.syhler.android.messagingapp.R
import androidx.lifecycle.Observer
import com.syhler.android.messagingapp.authenticate.CurrentUser
import com.syhler.android.messagingapp.authenticate.FacebookAuth
import com.syhler.android.messagingapp.authenticate.GoogleAuth
import com.syhler.android.messagingapp.authenticate.enums.AuthenticationMethod
import com.syhler.android.messagingapp.data.Database
import com.syhler.android.messagingapp.data.repos.ChatRoomRepository
import kotlinx.android.synthetic.main.activity_rooms.*
import kotlinx.android.synthetic.main.content_rooms.*
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class RoomActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var  googleAuth : GoogleAuth
    private val facebookAuth: FacebookAuth = FacebookAuth()
    private var currentUser : CurrentUser = CurrentUser()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rooms)


        googleAuth = GoogleAuth(this, getString(R.string.default_web_client_id))

        val chatRoomAdapter = ChatRoomAdapter(this, Database.chatRooms)

        findViewById<ListView>(R.id.list_chat).adapter = chatRoomAdapter

/*
        listView_chat_rooms.setOnItemClickListener{adapterView, view, position: Int, id: Long ->
            run {
                Toast.makeText(this, "You clicked at : $position", Toast.LENGTH_LONG).show()
            }
        }

 */




        currentUser.getProfileImage().invokeOnCompletion {
            updateImage(currentUser.image)
        }


    }

    private fun updateImage(bitmap: Bitmap)
    {
        GlobalScope.launch(Main) {
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
