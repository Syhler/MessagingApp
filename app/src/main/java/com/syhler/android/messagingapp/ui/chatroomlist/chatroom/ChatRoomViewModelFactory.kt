package com.syhler.android.messagingapp.ui.chatroomlist.chatroom

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.syhler.android.messagingapp.data.repos.MessageRepository

@Suppress("UNCHECKED_CAST")
class ChatRoomViewModelFactory(private val messageRepository: MessageRepository)
    : ViewModelProvider.NewInstanceFactory()
{

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ChatRoomViewModel(messageRepository) as T
    }

}