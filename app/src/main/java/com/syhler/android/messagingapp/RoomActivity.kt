package com.syhler.android.messagingapp

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import com.syhler.android.messagingapp.authenticate.CurrentUser
import com.syhler.android.messagingapp.authenticate.FacebookAuth
import com.syhler.android.messagingapp.authenticate.GoogleAuth
import com.syhler.android.messagingapp.authenticate.enums.AuthenticationMethod
import com.syhler.android.messagingapp.data.Database
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
        setSupportActionBar(toolbar)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        googleAuth = GoogleAuth(this, getString(R.string.default_web_client_id))


        temp_message.text = currentUser.fullName

        currentUser.getProfileImage().invokeOnCompletion {
            updateImage(currentUser.image)
        }

        button_log_out.setOnClickListener(this)
        button2.setOnClickListener {
            Database.getInstance().loadData()
        }
    }

    private fun updateImage(bitmap: Bitmap)
    {
        GlobalScope.launch(Main) {
            profile_pic.setImageBitmap(bitmap)
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
