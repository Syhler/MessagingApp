package com.syhler.android.messagingapp.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.syhler.android.messagingapp.data.entites.Message
import com.syhler.android.messagingapp.data.repos.MessageRepository
import com.syhler.android.messagingapp.utillities.DateManipulation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ChatRoomViewModel(private val messageRepository: MessageRepository) : ViewModel()
{
    private var TAG = "CHATROOM_VIEWMODEL"

    private var messages : MutableLiveData<List<Message>> = MutableLiveData()
    var loadedAllMessages : Boolean = false
    var latestMessage : MutableLiveData<Message?> = MutableLiveData()

    fun getInitMessages() : LiveData<List<Message>>
    {
        messageRepository.getInitMessages(20).get().addOnSuccessListener {
            val messagesList : MutableList<Message> = mutableListOf()

            for (doc in it) {
                messagesList.add(createMessageFromDoc(doc))
            }

            messages.value = messagesList

        }.addOnCompleteListener {
            getLatestMessages(messages.value?.get(messages.value!!.size -1)?.timespan!!)
        }

        return messages
    }


    fun loadPreviousMessages(): Task<QuerySnapshot> {
        return messageRepository.getMessageFrom(getFirstMessage()?.timespan!!, 50).get().addOnSuccessListener {
            loadedAllMessages = it.isEmpty || it.size() < 50


            val newMessages = mutableListOf<Message>()
            for (doc in it)
            {
                newMessages.add(createMessageFromDoc(doc))
            }

            newMessages.addAll(messages.value!!)
            if (newMessages.count() != messages.value?.count())
            {
                messages.value = newMessages
            }
        }
    }

    fun addMessage(message: Message)
    {
        messageRepository.addMessage(message)
    }


    private fun getFirstMessage() : Message?
    {
        return messages.value?.get(0)
    }

    private fun getLatestMessages(fromTimeStamp: Long)
    {
        messageRepository.getLatestMessage(fromTimeStamp).addSnapshotListener(EventListener<QuerySnapshot> { value, e ->
            if (e != null) {
                Log.w(TAG, "EventListener failed",e)
                return@EventListener
            }
            var message : Message? = null
            for (doc in value!!) {
                message = createMessageFromDoc(doc)
            }
            latestMessage.value = message
        })
    }

    private fun createMessageFromDoc(doc : QueryDocumentSnapshot) : Message
    {
        val tempMessage = doc.toObject(Message::class.java)
        tempMessage.date = DateManipulation.convertTimespanToDate(tempMessage.timespan)
        return tempMessage
    }

}
