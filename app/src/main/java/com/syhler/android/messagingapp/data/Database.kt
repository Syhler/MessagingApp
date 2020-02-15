package com.syhler.android.messagingapp.data

import com.google.firebase.firestore.FirebaseFirestore
import com.syhler.android.messagingapp.data.entites.ChatRoom

class Database
{

    companion object
    {
        private var instance : Database? = null

        fun getInstance() : Database
        {
            return if (instance == null) {
                Database()
            } else {
                instance!!
            }
        }
    }

    fun setupPredefinedChatRooms()
    {
        val database = FirebaseFirestore.getInstance()
        val chatRoomsPrefix = "chat_rooms"

        val chatRoom = ChatRoom("First chat room", "This is a chat room with stuff")
        val chatRoom2 = ChatRoom( "Second chat room", "This is a chat room with stuff")
        val chatRoom3 = ChatRoom( "Third chat room", "This is a chat room with stuff")

        database.document("$chatRoomsPrefix/4499b251-faf8-45f7-9f2e-a83a0f35fb50").set(chatRoom)
        database.document("$chatRoomsPrefix/bc34617e-fe22-468f-993d-8b202fca7788").set(chatRoom2)
        database.document("$chatRoomsPrefix/f60c7c5f-e2ed-46b0-acbe-f2eee9e4bacb").set(chatRoom3)
    }

    private fun getCurrentTimeSpan() : Long
    {
        return (System.currentTimeMillis())

    }

}