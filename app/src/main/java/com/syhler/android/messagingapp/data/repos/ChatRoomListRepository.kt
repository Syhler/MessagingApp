package com.syhler.android.messagingapp.data.repos

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query


class ChatRoomListRepository
{

    private val CHATROOM_PREFIX = "chat_rooms"


    private var database = FirebaseFirestore.getInstance()


    fun getChatRooms() : CollectionReference
    {
        return database.collection(CHATROOM_PREFIX)
    }

    fun getLatestMessageSingular(chatRoomId : String) : Query
    {
        return database.collection("$CHATROOM_PREFIX/$chatRoomId/messages").orderBy("timespan").limitToLast(1)
    }
}

