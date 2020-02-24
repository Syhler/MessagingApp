package com.syhler.android.messagingapp.viewmodels

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.syhler.android.messagingapp.data.entites.Message
import com.syhler.android.messagingapp.data.repos.MessageRepository
import com.syhler.android.messagingapp.utillities.DateManipulation


class ChatRoomViewModel(private val messageRepository: MessageRepository) : ViewModel()
{
    private var TAG = "CHATROOM_VIEWMODEL"

    private val loadSize = 50

    private var messages : MutableLiveData<List<Message>> = MutableLiveData()
    var loadedAllMessages : Boolean = false
    var latestMessage : MutableLiveData<Message?> = MutableLiveData()

    fun getInitMessages() : LiveData<List<Message>>
    {

        messageRepository.getInitMessages(loadSize.toLong()).get().addOnSuccessListener {
            val messagesList : MutableList<Message> = mutableListOf()

            for (doc in it) {
                messagesList.add(createMessageFromDoc(doc))
            }

            messages.value = messagesList

        }.addOnCompleteListener{
            activateIncomingMessages()
        }

        return messages
    }



    fun loadPreviousMessages(currentMessage : List<Message>): Task<QuerySnapshot>?
    {
        val firstMessage: Message? = getFirstMessage() ?: return null
        if (messages.value?.size!! < loadSize) return null


        return messageRepository.getMessageFrom(firstMessage?.timespan!!, loadSize.toLong()).get().addOnSuccessListener {
            loadedAllMessages = it.isEmpty || it.size() < loadSize


            val newMessages = mutableListOf<Message>()
            for (doc in it)
            {
                newMessages.add(createMessageFromDoc(doc))
            }

            newMessages.addAll(currentMessage)
            if (newMessages.count() != currentMessage.size)
            {
                messages.value = newMessages
            }
        }


    }

    fun addMessage(message: Message)
    {
        if (!message.imageUri.isBlank()) {
            uploadImage(message)
        }
        else{
            messageRepository.addMessage(message)
        }

    }

    private fun activateIncomingMessages()
    {
        if (messages.value!!.size-1 > 0)
        {
            loadIncomingMessages(messages.value?.get(messages.value!!.size -1)?.timespan!!)
        }
        else
        {
            loadIncomingMessages(0)
        }
    }

    private fun loadIncomingMessages(lastMessageLoaded : Long)
    {
        messageRepository.getMessagesFromNoLimit(lastMessageLoaded).addSnapshotListener { snapshot, e ->
            if (e != null)
            {
                //error
                return@addSnapshotListener
            }

            for (documentChange in snapshot?.documentChanges!!)
            {
                if (documentChange.type == DocumentChange.Type.ADDED)
                {
                    val message = createMessageFromDoc(documentChange.document)
                    latestMessage.value = message
                }
            } }
    }



    private fun uploadImage(message: Message)
    {
        val imageUri = Uri.parse(message.imageUri)

        if (imageUri != Uri.EMPTY) {

            messageRepository.uploadImage(imageUri)
                .addOnSuccessListener {taskSnapshot ->
                    message.imageUri = taskSnapshot?.storage!!.toString()
                    messageRepository.addMessage(message)
                }.addOnFailureListener {
                    Log.e(TAG, "Upload of image threw an exception", it)
                }
        }
    }


    private fun getFirstMessage() : Message?
    {
        if (messages.value?.size!! > 0)
        {
            return messages.value?.get(0)
        }
        return null
    }

    private fun createMessageFromDoc(doc : QueryDocumentSnapshot) : Message
    {
        val tempMessage = doc.toObject(Message::class.java)
        tempMessage.date = DateManipulation.convertTimespanToDate(tempMessage.timespan)
        tempMessage.imageUri
        return tempMessage
    }

}
