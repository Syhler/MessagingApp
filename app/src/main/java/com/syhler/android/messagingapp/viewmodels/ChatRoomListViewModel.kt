package com.syhler.android.messagingapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.syhler.android.messagingapp.data.entites.ChatRoom
import com.syhler.android.messagingapp.data.entites.Message
import com.syhler.android.messagingapp.data.repos.ChatRoomListRepository
import com.syhler.android.messagingapp.utillities.DateManipulation


class ChatRoomListViewModel(private val repository: ChatRoomListRepository) : ViewModel()
{
    private var chatRooms : MutableLiveData<List<ChatRoom>> = MutableLiveData()

    fun getChatRooms() : LiveData<List<ChatRoom>>
    {
        updateChatRooms()
        return chatRooms
    }

    fun updateChatRooms(): Task<QuerySnapshot> {
        val tempChatRooms = mutableListOf<ChatRoom>()

        return repository.getChatRooms().get().addOnSuccessListener {

            for (doc in it.documents)
            {
                tempChatRooms.add(createChatRoomFromDoc(doc))
            }

        }.addOnCompleteListener {

            getLatestMessageFromRooms(tempChatRooms)
        }
    }

    private fun getLatestMessageFromRooms(chatRooms: MutableList<ChatRoom>)
    {

        val queries = getQueryForLatestMessage(chatRooms)

        Tasks.whenAllComplete(queries).addOnSuccessListener {

            val sortedChatRooms = chatRooms.sortedByDescending { x -> x.getLatestMessageDate() }

            this.chatRooms.postValue(sortedChatRooms)
        }
    }

    /**
     * Creates the queries to get the latest image from a list of chat rooms
     */
    private fun getQueryForLatestMessage(chatRooms : List<ChatRoom>) : List<Task<QuerySnapshot>>
    {
        val data : MutableList<Task<QuerySnapshot>> = arrayListOf()

        for (room in chatRooms)
        {
            val query = repository.getLatestMessage(room.key).get().addOnSuccessListener {
                if (it.size() > 0)
                {
                    room.messages.add(createMessageFromDoc(it.documents[0]))
                }
            }
            data.add(query)
        }
        return data
    }

    private fun createMessageFromDoc(doc : DocumentSnapshot) : Message
    {
        val tempMessage = doc.toObject(Message::class.java)!!
        tempMessage.date = DateManipulation.convertTimespanToDate(tempMessage.timespan)
        return tempMessage
    }

    private fun createChatRoomFromDoc(doc : DocumentSnapshot) : ChatRoom
    {
        val tempChatRoom = doc.toObject(ChatRoom::class.java)!!
        tempChatRoom.key = doc.id
        return tempChatRoom
    }






}