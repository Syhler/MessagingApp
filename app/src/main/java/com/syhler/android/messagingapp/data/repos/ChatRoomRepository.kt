package com.syhler.android.messagingapp.data.repos

import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.syhler.android.messagingapp.data.Database
import com.syhler.android.messagingapp.data.entites.ChatRoom


class ChatRoomRepository()
{

    private val CHATROOM_PREFIX = "chat_rooms"


    var database = FirebaseFirestore.getInstance()


    fun getChatRooms() : Query
    {
        val collectionReference = database.collectionGroup(CHATROOM_PREFIX)
        return collectionReference
    }

    fun getMessage(chatRoomId : String) : CollectionReference
    {
        val collectionReference = database.collection("$CHATROOM_PREFIX/$chatRoomId/messages")
        return collectionReference
    }




/*
    fun getRoomNamesByLatestMessage() : MutableLiveData<MutableList<ChatRoom>>
    {
        /*
        val chatRoom = Database.chatRooms
        val stuff = chatRoom.value?.sortedBy { n -> n.getLatestMessageDate() }
        //chatRoom?.value = (stuff as MutableList<ChatRoom>?)!!
        //return chatRoom

         */
        return Database.getInstance().chatRooms
    }



 */


}