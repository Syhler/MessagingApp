package com.syhler.android.messagingapp.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.syhler.android.messagingapp.R
import com.syhler.android.messagingapp.authenticate.AuthenticationHandler
import com.syhler.android.messagingapp.authenticate.CurrentUser
import com.syhler.android.messagingapp.data.Database
import com.syhler.android.messagingapp.ui.chatroomlist.ChatRoomListActivity
import kotlinx.android.synthetic.main.main_activity.*


class MainActivity : AppCompatActivity()
{

    private lateinit var authenticationHandler : AuthenticationHandler

    override fun onStart() {
        super.onStart()

        if (CurrentUser.getInstance().isloggedIn) {
            changeScene()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.main_activity)


        Database.getInstance().setupPredefinedChatRooms()

        authenticationHandler = AuthenticationHandler(this, getString(R.string.default_web_client_id))

        setupFacebookLogin()

        button_google_login.setOnClickListener { authenticationHandler.google.onSignInClicked(this) }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        signInGoogle(requestCode, data)
        authenticationHandler.facebook.callbackManager.onActivityResult(requestCode, resultCode, data)

    }

    private fun setupFacebookLogin()
    {
        button_facebook_login.setPermissions("email", "public_profile")
        button_facebook_login.registerCallback(authenticationHandler.facebook.callbackManager, createFacebookCallback())
    }

    private fun createFacebookCallback() : FacebookCallback<LoginResult>
    {
        return object:FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult?) {
                if (result != null) {
                    authenticationHandler.facebook.handleFacebookAccessToken(result.accessToken).addOnCompleteListener {
                        CurrentUser.initialize() //instantiate current user
                        showToast("Login succeeded")
                        changeScene()
                    }
                }
            }
            override fun onCancel() {
                showToast("Facebook login is cancel")
            }

            override fun onError(error: FacebookException?) {
                showToast("Couldn't login to facebook. ")
            }
        }
    }

    private fun signInGoogle(requestCode: Int, data: Intent?)
    {
        //make a null check
        authenticationHandler.google.signInGoogle(requestCode, data)?.addOnCompleteListener(this) { task ->
        if (task.isSuccessful) {
            CurrentUser.initialize() //instantiate current user
            changeScene()
            showToast("Login succeeded")
        } else {
            // If sign in fails, display a message to the user.
            showToast("Login failed")
        }}
    }

    private fun changeScene()
    {
        val intent = Intent(this, ChatRoomListActivity::class.java)
        startActivity(intent)
    }

    private fun showToast(text : String)
    {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()

    }









}




























