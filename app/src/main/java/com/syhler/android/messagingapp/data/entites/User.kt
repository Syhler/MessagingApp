package com.syhler.android.messagingapp.data.entites

data class User(
    val image: String?,
    val fullName: String,
    var userAuthID : String)
{
    constructor() : this(null, "", "")

    fun stringToBitmap()
    {

    }
}