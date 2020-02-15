package com.syhler.android.messagingapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.Profile
import com.facebook.login.LoginResult
import com.google.firebase.auth.FirebaseAuth
import com.syhler.android.messagingapp.authenticate.FacebookAuth
import com.syhler.android.messagingapp.authenticate.GoogleAuth
import com.syhler.android.messagingapp.data.Database
import com.syhler.android.messagingapp.ui.chatroom.ChatRoomActivity
import kotlinx.android.synthetic.main.main_activity.*


class MainActivity : AppCompatActivity()
{

    private lateinit var googleAuth: GoogleAuth
    private val facebookAuth = FacebookAuth()

    override fun onStart() {
        super.onStart()
        if (FirebaseAuth.getInstance().currentUser != null || Profile.getCurrentProfile() != null)
        {
            changeScene()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //FacebookSdk.sdkInitialize(applicationContext)

        setContentView(R.layout.main_activity)
        if (savedInstanceState == null)
        {
            supportFragmentManager.beginTransaction().commitNow()
        }

        Database.getInstance().setup()

        setupFacebookLogin()

        setupGoogleLogin()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        signInGoogle(requestCode, data)
        facebookAuth.callbackManager.onActivityResult(requestCode, resultCode, data)

    }

    private fun setupFacebookLogin()
    {
        button_facebook_login.setPermissions("email", "public_profile")
        button_facebook_login.registerCallback(facebookAuth.callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult?) {
                    if (result != null) {
                        facebookAuth.handleFacebookAccessToken(result.accessToken).addOnCompleteListener {
                            showToast("Login succeeded")
                            changeScene()
                        }
                    }
                }

                override fun onCancel() {
                    showToast("Facebook login in cancel")
                }

                override fun onError(error: FacebookException?) {
                    showToast("Couldn't login to facebook. ")
                }

            })
    }

    private fun setupGoogleLogin()
    {
        //Google
        googleAuth = GoogleAuth(this, getString(R.string.default_web_client_id))

        button_google_login.setOnClickListener { googleAuth.signIn(this) }
    }

    private fun signInGoogle(requestCode: Int, data: Intent?)
    {
        //make a null check

        googleAuth.signInGoogle(requestCode, data)?.addOnCompleteListener(this) { task ->
        if (task.isSuccessful) {
            // Sign in success, update UI with the signed-in user's information
            //message.text = "User logged in : ${googleAuth.firebaseAuth.currentUser?.email}"
            changeScene()
            showToast("Login succeeded")
        } else {
            // If sign in fails, display a message to the user.
            showToast("Login failed")
        }}
    }

    private fun changeScene()
    {
        val intent = Intent(this, ChatRoomActivity::class.java)
        startActivity(intent)
    }

    private fun showToast(text : String)
    {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()

    }









}




























