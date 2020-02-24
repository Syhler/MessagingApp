package com.syhler.android.messagingapp.authenticate

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.app.ActivityCompat.startActivityForResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class GoogleAuth(private val context: Context?, requestIdToken: String, private val dialog: androidx.appcompat.app.AlertDialog?)
{


    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN : Int = 101

    init {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(requestIdToken)
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(context!!, gso)


    }

    fun onSignInClicked(activity : Activity?)
    {
        googleSignInClient.signInIntent.also {
            if (activity != null) {
                startActivityForResult(activity, it,RC_SIGN_IN,  null)
            }
        }
    }

    fun signInGoogle(requestCode: Int, data: Intent?) : Task<AuthResult>?
    {
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            return try {
                // Google Sign In was successful
                val account = task.getResult(ApiException::class.java)
                signInfirebaseAuth(account!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                dialog?.dismiss()
                Toast.makeText(context, "Google login failed", Toast.LENGTH_LONG).show()
                null
            }
        }
        return null
    }

    private fun signInfirebaseAuth(acct: GoogleSignInAccount) : Task<AuthResult>
    {

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        return firebaseAuth.signInWithCredential(credential)
    }


    fun signOut(): Task<Void>? {
        // Firebase sign out
        firebaseAuth.signOut()

        // Google sign out
        return googleSignInClient.signOut()
    }


}