package com.syhler.android.messagingapp.viewmodels

import android.content.ContentResolver
import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.syhler.android.messagingapp.data.entites.Message
import com.syhler.android.messagingapp.data.repos.MessageRepository
import com.syhler.android.messagingapp.utillities.DateManipulation


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

        }.addOnCompleteListener{
            if (messages.value!!.size-1 > 0)
            {
                getLatestMessages(messages.value?.get(messages.value!!.size -1)?.timespan!!)
            }
        }

        return messages
    }


    fun loadPreviousMessages(): Task<QuerySnapshot>?
    {
        val firstMessage: Message? = getFirstMessage() ?: return null

        return messageRepository.getMessageFrom(firstMessage?.timespan!!, 50).get().addOnSuccessListener {
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

    private fun uploadImage(message: Message)
    {
        val imageUri = Uri.parse(message.imageUri)

        if (imageUri != Uri.EMPTY) {

            messageRepository.uploadImage(imageUri)
                .addOnSuccessListener {taskSnapshot ->
                    message.imageUri = taskSnapshot?.storage!!.toString()
                    messageRepository.addMessage(message)
                }.addOnFailureListener {
                    Log.e(TAG, "something went wrong", it)
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


    private fun getFirstMessage() : Message?
    {
        if (messages.value?.size!! > 0)
        {
            return messages.value?.get(0)
        }
        return null
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
        tempMessage.imageUri
        return tempMessage
    }


    private fun getImage(uri : String)
    {
        val imageUri = Uri.parse(uri)
        val result = messageRepository.getImage(imageUri).result


    }

}
