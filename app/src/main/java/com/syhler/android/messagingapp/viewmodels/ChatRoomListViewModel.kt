package com.syhler.android.messagingapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.QuerySnapshot
import com.syhler.android.messagingapp.data.entites.ChatRoom
import com.syhler.android.messagingapp.data.entites.Message
import com.syhler.android.messagingapp.data.repos.ChatRoomListRepository
import com.syhler.android.messagingapp.utillities.DateManipulation


class ChatRoomListViewModel : ViewModel()
{
    private val TAG = "CHATROOM_VIEW_MODEL"
    private val repo = ChatRoomListRepository()
    private var chatRooms : MutableLiveData<List<ChatRoom>> = MutableLiveData()

    fun getChatRooms() : LiveData<List<ChatRoom>>
    {
        val tempChatRooms = mutableListOf<ChatRoom>()

        repo.getChatRooms().get().addOnSuccessListener {
            //val chatRoomList : MutableList<ChatRoom> = mutableListOf()
            it.documents.forEach{ doc ->
                val tempChatRoom = doc.toObject(ChatRoom::class.java)!!
                tempChatRoom.key = doc.id
                tempChatRooms.add(tempChatRoom)
            }
            //chatRooms.value = chatRoomList
        }.addOnCompleteListener {
            getLatestMessage(tempChatRooms)
        }

        return chatRooms

    }

    private fun getLatestMessage(chatRooms: MutableList<ChatRoom>)
    {
        val queries = getQueryForLatestMessage(chatRooms)

        Tasks.whenAllComplete(queries).addOnSuccessListener {

            val sortedChatRooms = chatRooms.sortedByDescending { x -> x.getLatestMessageDate() }

            this.chatRooms.postValue(sortedChatRooms)
        }
    }

    private fun getQueryForLatestMessage(chatRooms : List<ChatRoom>) : List<Task<QuerySnapshot>>
    {
        val data : MutableList<Task<QuerySnapshot>> = arrayListOf()

        for (room in chatRooms)
        {
            val query = repo.getLatestMessageSingular(room.key).get().addOnSuccessListener {
                if (it.size() > 0)
                {
                    val tempMessage = it.documents[0].toObject(Message::class.java)!!
                    tempMessage.date = DateManipulation.convertTimespanToDate(tempMessage.timespan)
                    room.messages.add(tempMessage)
                }
            }
            data.add(query)
        }
        return data
    }






}