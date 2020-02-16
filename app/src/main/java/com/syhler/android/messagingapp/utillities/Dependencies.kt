package com.syhler.android.messagingapp.utillities

import com.syhler.android.messagingapp.data.repos.ChatRoomListRepository
import com.syhler.android.messagingapp.data.repos.MessageRepository
import com.syhler.android.messagingapp.viewmodels.factory.ChatRoomListViewModelFactory
import com.syhler.android.messagingapp.viewmodels.factory.ChatRoomViewModelFactory

object Dependencies
{
    fun provideChatRoomViewModelFactory(chatRoomKey : String) : ChatRoomViewModelFactory
    {
        val repo = MessageRepository(chatRoomKey)
        return ChatRoomViewModelFactory(repo)
    }

    fun provideChatRoomListViewModelFactory() : ChatRoomListViewModelFactory
    {
        val repo = ChatRoomListRepository()
        return ChatRoomListViewModelFactory(repo)
    }
}