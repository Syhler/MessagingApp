package com.syhler.android.messagingapp.data.repos

import androidx.lifecycle.MutableLiveData
import com.syhler.android.messagingapp.data.Database
import com.syhler.android.messagingapp.data.entites.ChatRoom


class RoomRepository()
{
    private val chatRooms : MutableLiveData<MutableList<ChatRoom>> = Database.getInstance().chatRooms


    fun getRoomNamesByLatestMessage() : MutableLiveData<MutableList<ChatRoom>>
    {
        TODO()
    }


}