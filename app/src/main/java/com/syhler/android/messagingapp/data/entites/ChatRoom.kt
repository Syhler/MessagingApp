package com.syhler.android.messagingapp.data.entites

import com.google.firebase.firestore.Exclude
import java.sql.Timestamp
import java.util.*

data class ChatRoom(val name : String,
                    val description : String)
{

    @set:Exclude @get:Exclude var key : String = ""
    @get:Exclude val messages : MutableList<Message> = arrayListOf()

    constructor() : this("", "")

    fun getLatestMessageDate() : Long
    {
        if (messages.size > 0)
        {
            return messages[0].timespan
        }
        return 0
    }


}
