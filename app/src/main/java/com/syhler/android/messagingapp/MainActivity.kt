package com.syhler.android.messagingapp

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.syhler.android.messagingapp.authenticate.GoogleAuth
import kotlinx.android.synthetic.main.main_activity.*

class MainActivity : AppCompatActivity()
{

    private lateinit var googleAuth: GoogleAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                //.replace(R.id.container, MainFragment.newInstance())
                .commitNow()
        }

        googleAuth = GoogleAuth(this, getString(R.string.default_web_client_id))

        button_login.setOnClickListener { googleAuth.signIn(this) }
        button_log_out.setOnClickListener {
            googleAuth.signOut()?.addOnCompleteListener(this) {
                message.text = "offline"
                showToast("Logged off")
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        signInGoogle(requestCode, data)

    }

    private fun signInGoogle(requestCode: Int, data: Intent?)
    {
        //make a null check

        googleAuth.signInGoogle(requestCode, data)?.addOnCompleteListener(this) { task ->
        if (task.isSuccessful) {
            // Sign in success, update UI with the signed-in user's information
            Log.d(ContentValues.TAG, "signInWithCredential:success")
            message.text = "User logged in : ${googleAuth.firebaseAuth.currentUser?.email}"
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




























