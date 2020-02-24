package com.syhler.android.messagingapp.data

import com.google.firebase.firestore.FirebaseFirestore
import com.syhler.android.messagingapp.data.entites.ChatRoom

class FakeDatabaseSetup
{

    companion object
    {
        private var instance : FakeDatabaseSetup? = null

        fun getInstance() : FakeDatabaseSetup
        {
            return if (instance == null) {
                FakeDatabaseSetup()
            } else {
                instance!!
            }
        }
    }

    fun setupPredefinedChatRooms()
    {
        val database = FirebaseFirestore.getInstance()
        val chatRoomsPrefix = "chat_rooms"

        val chatRoom = ChatRoom("First chat room", "This is a chat room with stuff", "https://www.shareicon.net/data/512x512/2017/03/01/880148_media_512x512.png")
        val chatRoom2 = ChatRoom( "Second chat room", "This is a chat room with stuff", "https://cdn4.iconfinder.com/data/icons/miu-flat-social/60/window_store-512.png")
        val chatRoom3 = ChatRoom( "Third chat room", "This is a chat room with stuff", "https://purepng.com/public/uploads/large/21502362885rmhziap3wm5w0jogfdubr1fgyzuycu5rqkam39wjhh7yhmcncxka3vxq3xglitq4iwze8v0gpi1wmolyrtqkts57kit8ibyd2apb.png")

        database.document("$chatRoomsPrefix/4499b251-faf8-45f7-9f2e-a83a0f35fb50").set(chatRoom)
        database.document("$chatRoomsPrefix/bc34617e-fe22-468f-993d-8b202fca7788").set(chatRoom2)
        database.document("$chatRoomsPrefix/f60c7c5f-e2ed-46b0-acbe-f2eee9e4bacb").set(chatRoom3)
    }

    private fun getCurrentTimeSpan() : Long
    {
        return (System.currentTimeMillis())

    }

}