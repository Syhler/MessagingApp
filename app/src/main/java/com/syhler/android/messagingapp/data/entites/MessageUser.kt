package com.syhler.android.messagingapp.data.entites

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import com.google.firebase.firestore.Exclude


data class MessageUser(
    val image: String?,
    val fullName: String,
    var userAuthID : String)
{
    constructor() : this(null, "", "")



}