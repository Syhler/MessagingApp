package com.syhler.android.messagingapp.data.entites

data class User(val image: String?, val fullName: String)
{
    constructor() : this(null, "")

    fun stringToBitmap()
    {

    }
}