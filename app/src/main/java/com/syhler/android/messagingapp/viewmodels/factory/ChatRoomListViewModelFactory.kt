package com.syhler.android.messagingapp.viewmodels.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.syhler.android.messagingapp.data.repos.ChatRoomListRepository
import com.syhler.android.messagingapp.viewmodels.ChatRoomListViewModel

@Suppress("UNCHECKED_CAST")
class ChatRoomListViewModelFactory(private val chatRoomListRepository: ChatRoomListRepository) : ViewModelProvider.NewInstanceFactory()
{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ChatRoomListViewModel(chatRoomListRepository) as T
    }
}