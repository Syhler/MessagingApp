package com.syhler.android.messagingapp.data.repos

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore


class ChatRoomListRepository
{

    private val CHATROOM_PREFIX = "chat_rooms"


    private var database = FirebaseFirestore.getInstance()


    fun getChatRooms() : CollectionReference
    {
        return database.collection(CHATROOM_PREFIX)
    }

    fun getMessage(chatRoomId : String) : CollectionReference
    {
        return database.collection("$CHATROOM_PREFIX/$chatRoomId/messages")
    }
}

