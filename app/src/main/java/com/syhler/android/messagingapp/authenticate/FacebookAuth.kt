package com.syhler.android.messagingapp.authenticate

import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.login.LoginManager
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth

class FacebookAuth
{
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()

    var callbackManager: CallbackManager = CallbackManager.Factory.create()


    fun signOut()
    {
        auth.signOut()
        LoginManager.getInstance().logOut()
    }

    fun handleFacebookAccessToken(token: AccessToken) : Task<AuthResult>
    {
        val credential = FacebookAuthProvider.getCredential(token.token)
        return auth.signInWithCredential(credential)
    }

}