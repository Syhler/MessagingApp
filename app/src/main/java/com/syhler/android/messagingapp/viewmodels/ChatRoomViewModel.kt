package com.syhler.android.messagingapp.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.QuerySnapshot
import com.syhler.android.messagingapp.data.entites.ChatRoom
import com.syhler.android.messagingapp.data.entites.Message
import com.syhler.android.messagingapp.data.repos.ChatRoomRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.sql.Timestamp
import java.util.*


class ChatRoomViewModel : ViewModel()
{
    private val TAG = "CHATROOM_VIEW_MODEL"
    private val repo = ChatRoomRepository()
    private var chatRooms : MutableLiveData<List<ChatRoom>> = MutableLiveData()


    fun getChatRooms() : LiveData<List<ChatRoom>>
    {
        repo.getChatRooms().addSnapshotListener(EventListener<QuerySnapshot>{ value, e ->
            if (e != null)
            {
                Log.w(TAG, "EventListener failed",e)
                return@EventListener
            }

            val chatRoomList : MutableList<ChatRoom> = mutableListOf()
            for (doc in value!!)
            {
                val tempChatRoom = doc.toObject(ChatRoom::class.java)
                tempChatRoom.key = doc.id
                chatRoomList.add(tempChatRoom)
            }


            chatRoomList.sortBy { i -> i.getLatestMessageDate() }
            chatRooms.value = chatRoomList

        })
        return chatRooms
    }

    fun getChatRooms2() : LiveData<List<ChatRoom>>
    {
        repo.getChatRooms().get().addOnSuccessListener {
            val chatRoomList : MutableList<ChatRoom> = mutableListOf()
            it.documents.forEach { doc ->
                val tempChatRoom = doc.toObject(ChatRoom::class.java)!!
                tempChatRoom?.key = doc.id
                chatRoomList.add(tempChatRoom)
            }
            chatRooms.value = chatRoomList
        }.addOnCompleteListener {
            getLatestMessage()
        }

        return chatRooms

    }

    private fun getLatestMessage()
    {
        val tempChatRooms = chatRooms.value!!

        val data : MutableList<Task<QuerySnapshot>> = arrayListOf()

        for (room in tempChatRooms)
        {

            val query = repo.getMessage(room.key).limit(1).get().addOnSuccessListener {
                if (it.size() > 0)
                {
                    val tempMessage = it.documents[0].toObject(Message::class.java)!!
                    tempMessage.date = convertTimespanToDate(tempMessage.timespan)
                    room.messages.add(tempMessage)
                }
            }

            data.add(query)
        }

        Tasks.whenAllComplete(data).addOnSuccessListener {

            val sortedChatRooms = tempChatRooms.sortedByDescending { x -> x.getLatestMessageDate() }


            chatRooms.postValue(sortedChatRooms)
        }

    }

    private fun convertTimespanToDate(timestamp: Long) : Date
    {
        val ts = Timestamp(timestamp)
        return Date(ts.time)
    }




}