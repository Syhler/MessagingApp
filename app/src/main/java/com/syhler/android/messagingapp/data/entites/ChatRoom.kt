package com.syhler.android.messagingapp.data.entites

data class ChatRoom(val messages : List<Message>,
                    val name : String,
                    val description : String)
{
    constructor() : this(arrayListOf(), "", "")
}
