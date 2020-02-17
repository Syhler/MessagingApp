package com.syhler.android.messagingapp.data.repos

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.syhler.android.messagingapp.data.entites.Message
import java.util.*

class MessageRepository(chatRoomKey : String)
{
    private val CHATROOM_PREFIX = "chat_rooms"
    private val messageCollectionPath = "$CHATROOM_PREFIX/$chatRoomKey/messages"

    private var database = FirebaseFirestore.getInstance()

    fun getInitMessages(numberOfMessages : Long) : Query
    {
        return database.collection(messageCollectionPath)
            .orderBy("timespan")
            .limitToLast(numberOfMessages)
    }

    fun getLatestMessage(fromTimeStamp: Long): Query
    {
        return database.collection(messageCollectionPath)
            .orderBy("timespan")
            .whereGreaterThan("timespan", fromTimeStamp)
            .limitToLast(1)
    }

    fun getMessageFrom(fromTimeStamp : Long, limit : Long) : Query
    {
        return database.collection(messageCollectionPath)
            .orderBy("timespan")
            .whereLessThan("timespan", fromTimeStamp)
            .limit(limit)
    }

    fun addMessage(message: Message)
    {
        database.collection(messageCollectionPath).document(UUID.randomUUID().toString())
            .set(message)
    }




}