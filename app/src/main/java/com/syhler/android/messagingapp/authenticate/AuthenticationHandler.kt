package com.syhler.android.messagingapp.authenticate

import android.content.Context

class AuthenticationHandler(context: Context, requestIdToken: String)
{
    val facebook = FacebookAuth()
    val google = GoogleAuth(context, requestIdToken)
}