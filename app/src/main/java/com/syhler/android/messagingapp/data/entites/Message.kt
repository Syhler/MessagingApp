package com.syhler.android.messagingapp.data.entites

import java.util.*

data class Message(val text: String, val user: User, val date: String)
{
    constructor() : this("", User(),"")
}