package com.syhler.android.messagingapp.data.repos

import androidx.lifecycle.MutableLiveData
import com.syhler.android.messagingapp.data.Database
import com.syhler.android.messagingapp.data.entites.ChatRoom


class ChatRoomRepository()
{



    fun getRoomNamesByLatestMessage() : MutableLiveData<MutableList<ChatRoom>>
    {
        val chatRoom = Database.chatRooms
        val stuff = chatRoom.value?.sortedBy { n -> n.getLatestMessageDate() }
        //chatRoom?.value = (stuff as MutableList<ChatRoom>?)!!
        //return chatRoom
        return Database.chatRooms
    }


}