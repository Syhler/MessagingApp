package com.syhler.android.messagingapp.data.entites

import com.google.firebase.database.Exclude
import java.util.*


data class Message(val text: String,
                   val user: User,
                   val timespan: Long)
{

    @set:Exclude @get:Exclude var date : Date = Date(50)

    constructor() : this("", User(),0L)



}