package com.syhler.android.messagingapp.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.QuerySnapshot
import com.syhler.android.messagingapp.data.entites.Message
import com.syhler.android.messagingapp.data.repos.MessageRepository
import java.sql.Timestamp
import java.util.*

class ChatRoomViewModel(private val messageRepository: MessageRepository) : ViewModel()
{
    private var TAG = "CHATROOM_VIEWMODEL"

    private var messages : MutableLiveData<List<Message>> = MutableLiveData()
    var latestMessage : MutableLiveData<Message?> = MutableLiveData()

    fun getInitMessages() : LiveData<List<Message>>
    {
        messageRepository.getInitMessages(20).get().addOnSuccessListener {
            val messagesList : MutableList<Message> = mutableListOf()
            it.documents.forEach { doc ->
                val tempMessage = doc.toObject(Message::class.java)
                if (tempMessage != null)
                {
                    tempMessage.date = convertTimespanToDate(tempMessage.timespan)
                    messagesList.add(tempMessage)
                }
            }
            messages.value = messagesList
        }.addOnCompleteListener {
            getLatestMessages(messages.value?.get(messages.value!!.size -1)?.timespan!!)
        }

        return messages
    }

    private fun getLatestMessages(fromTimeStamp: Long)
    {
        messageRepository.getLatestMessage(fromTimeStamp).addSnapshotListener(EventListener<QuerySnapshot> { value, e ->
            if (e != null)
            {
                Log.w(TAG, "EventListener failed",e)
                return@EventListener
            }
            var message : Message? = null
            for (doc in value!!)
            {
                val tempMessage = doc.toObject(Message::class.java)
                tempMessage.date = convertTimespanToDate(tempMessage.timespan)
                message = tempMessage
            }
            latestMessage.value = message
        })
    }

    fun getMessagesFrom()
    {

    }


    fun addMessage(message: Message)
    {
        messageRepository.addMessage(message)
    }

    private fun convertTimespanToDate(timestamp: Long) : Date
    {
        val ts = Timestamp(timestamp)
        return Date(ts.time)
    }


}
