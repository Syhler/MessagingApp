package com.syhler.android.messagingapp.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.syhler.android.messagingapp.R
import com.syhler.android.messagingapp.authenticate.AuthenticationHandler
import com.syhler.android.messagingapp.authenticate.CurrentUser
import com.syhler.android.messagingapp.data.Database
import com.syhler.android.messagingapp.ui.chatroomlist.ChatRoomListActivity
import kotlinx.android.synthetic.main.login_activity.*


class LoginActivity : AppCompatActivity()
{

    private lateinit var authenticationHandler : AuthenticationHandler
    private lateinit var dialogLayout : View
    private lateinit var dialog : AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)

        createDialog()

        Database.getInstance().setupPredefinedChatRooms()

        authenticationHandler = AuthenticationHandler(this, getString(R.string.default_web_client_id), dialog)

        setupFacebookLogin()

        btn_google_login.setOnClickListener {
            authenticationHandler.google.onSignInClicked(this)
            dialog.show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        signInGoogle(requestCode, data)

        authenticationHandler.facebook.callbackManager.onActivityResult(requestCode, resultCode, data)

    }

    private fun setupFacebookLogin()
    {
        LoginManager.getInstance().registerCallback(authenticationHandler.facebook.callbackManager, createFacebookCallback())
        val btn = findViewById<Button>(R.id.btn_facebook_login)
        btn.setOnClickListener {
            LoginManager.getInstance().logInWithReadPermissions(this, arrayListOf("email", "public_profile"))
            dialog.show()
        }
    }

    private fun createDialog()
    {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogLayout = layoutInflater.inflate(R.layout.login_dialog, null)

        dialogBuilder.setView(dialogLayout)
        //alertBuilder.setPositiveButton("Ok") { _, _ -> }
        dialogBuilder.setCancelable(false)

        dialog = dialogBuilder.create()
    }

    private fun onLogin()
    {
        dialogLayout.findViewById<TextView>(R.id.auth_text_view).text = "Succeed"
        dialogLayout.findViewById<ProgressBar>(R.id.auth_progress_loader).visibility = View.GONE
    }


    private fun createFacebookCallback() : FacebookCallback<LoginResult>
    {
        return object:FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult?) {
                if (result != null) {
                    authenticationHandler.facebook.handleFacebookAccessToken(result.accessToken).addOnCompleteListener {
                        onLogin()
                        CurrentUser.initialize() //instantiate current user
                        changeScene()
                    }
                }
            }
            override fun onCancel() {
                dialog.dismiss()
                showToast("Facebook Login was cancel")
            }

            override fun onError(error: FacebookException?) {
                dialog.dismiss()
                showToast("Facebook Login Failed. ")
            }
        }
    }

    private fun signInGoogle(requestCode: Int, data: Intent?)
    {
        //make a null check
        authenticationHandler.google.signInGoogle(requestCode, data)?.addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                onLogin()
                CurrentUser.initialize() //instantiate current user
                changeScene()
            } else {
                // If sign in fails, display a message to the user.
                dialog.dismiss()
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




























