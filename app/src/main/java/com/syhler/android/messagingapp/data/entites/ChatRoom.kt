package com.syhler.android.messagingapp.data.entites

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import java.io.Serializable


@IgnoreExtraProperties
data class ChatRoom(val title : String,
                    val description : String,
                    val image : String)
{

    @set:Exclude @get:Exclude var key : String = ""
    @get:Exclude val messages : MutableList<Message> = arrayListOf()

    constructor() : this("", "", "")

    fun getLatestMessageDate() : Long
    {
        if (messages.size > 0)
        {
            return messages[0].timespan
        }
        return 0
    }


}
