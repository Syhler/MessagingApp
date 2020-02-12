package com.syhler.android.messagingapp

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.FacebookSdk
import com.facebook.login.LoginResult
import com.syhler.android.messagingapp.authenticate.FacebookAuth
import com.syhler.android.messagingapp.authenticate.GoogleAuth
import kotlinx.android.synthetic.main.main_activity.*

class MainActivity : AppCompatActivity()
{

    private lateinit var googleAuth: GoogleAuth
    private val facebookAuth = FacebookAuth()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FacebookSdk.sdkInitialize(applicationContext)

        setContentView(R.layout.main_activity)
        if (savedInstanceState == null)
        {
            supportFragmentManager.beginTransaction().commitNow()
        }


        //facebook
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
                        facebookAuth.handleFacebookAccessToken(result.accessToken)
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
        button_log_out.setOnClickListener {
            googleAuth.signOut()?.addOnCompleteListener(this) {
                showToast("Logged off")
            }
        }
    }

    private fun signInGoogle(requestCode: Int, data: Intent?)
    {
        //make a null check

        googleAuth.signInGoogle(requestCode, data)?.addOnCompleteListener(this) { task ->
        if (task.isSuccessful) {
            // Sign in success, update UI with the signed-in user's information
            //message.text = "User logged in : ${googleAuth.firebaseAuth.currentUser?.email}"
            showToast("Login succeeded")
        } else {
            // If sign in fails, display a message to the user.
            showToast("Login failed")
        }
        }
    }

    private fun showToast(text : String)
    {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()

    }









}




























