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

class GoogleAuth(val context: Context?, id : String)
{


    val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN : Int = 101

    init {
        //349551150180-i7ta2bvvl2l1a3m1ntvikrm5j4o427m4.apps.googleusercontent.com
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(id)
            .requestEmail()
            .build()

        //check for null

        googleSignInClient = GoogleSignIn.getClient(context!!, gso)


    }

    fun signIn(activity : Activity?)
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
            try {
                // Google Sign In was successful
                val account = task.getResult(ApiException::class.java)
                return firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(context, "Something went wrong", Toast.LENGTH_LONG).show()
            }
        }
        return null
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) : Task<AuthResult>  {

        //return signin with credential and add the "addoncompletelistner later"
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        return firebaseAuth.signInWithCredential(credential)
    }


    fun isLoggedIn() : Boolean
    {
        val currentUser = firebaseAuth.currentUser
        return currentUser != null
    }

    fun signOut(): Task<Void>? {
        // Firebase sign out
        firebaseAuth.signOut()

        // Google sign out
        return googleSignInClient.signOut()
    }


}