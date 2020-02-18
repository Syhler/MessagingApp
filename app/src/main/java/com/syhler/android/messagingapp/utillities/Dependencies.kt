package com.syhler.android.messagingapp.utillities

import android.content.ContentResolver
import com.syhler.android.messagingapp.data.repos.ChatRoomListRepository
import com.syhler.android.messagingapp.data.repos.MessageRepository
import com.syhler.android.messagingapp.viewmodels.factory.ChatRoomListViewModelFactory
import com.syhler.android.messagingapp.viewmodels.factory.ChatRoomViewModelFactory

object Dependencies
{
    fun provideChatRoomViewModelFactory(chatRoomKey : String, contentResolver: ContentResolver) : ChatRoomViewModelFactory
    {
        val repo = MessageRepository(chatRoomKey, contentResolver)
        return ChatRoomViewModelFactory(repo)
    }

    fun provideChatRoomListViewModelFactory() : ChatRoomListViewModelFactory
    {
        val repo = ChatRoomListRepository()
        return ChatRoomListViewModelFactory(repo)
    }
}