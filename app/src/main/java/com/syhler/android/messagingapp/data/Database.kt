package com.syhler.android.messagingapp.data

import android.graphics.Bitmap
import android.util.Base64
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.syhler.android.messagingapp.data.entites.ChatRoom
import com.syhler.android.messagingapp.data.entites.Message
import com.syhler.android.messagingapp.data.entites.User
import java.io.ByteArrayOutputStream
import java.sql.Timestamp
import java.util.*


class Database
{

    private val database = FirebaseFirestore.getInstance()
    private val CHATROOM_PREFIX = "chat_rooms"


    companion object
    {
        var chatRooms : MutableLiveData<MutableList<ChatRoom>> = MutableLiveData()

        var messages : MutableLiveData<MutableList<Message>> = MutableLiveData()


        @Volatile private var instance : Database? = null

        fun getInstance() = instance ?: synchronized(this)
        {
            instance ?: Database()
        }
    }

    fun setup()
    {
        predefinedChatRooms()
        loadChatRooms()
    }

    fun loadMessageFrom(fromTimestamp: Long, numberOfRecords: Long, chatRoomKey : String)
    {
        database.collection("$CHATROOM_PREFIX/$chatRoomKey/messages")
            .whereGreaterThan("timespan", fromTimestamp)
            .limit(numberOfRecords).get()
            .addOnSuccessListener { it ->
            it.documents.forEach {
                val tempMessage = it.toObject(Message::class.java)
                if (tempMessage != null) {
                    tempMessage.date = convertTimespanToDate(tempMessage.timespan)
                    messages.value?.add(tempMessage)
                }
            }
        }
    }

    private fun loadLatestMessageFromRooms()
    {
        chatRooms.value?.forEach {
            database.collection("$CHATROOM_PREFIX/${it.key}/messages").orderBy("timespan").get().addOnSuccessListener { query ->
                query.documents.forEach { i ->
                    val tempMessage = i.toObject(Message::class.java)
                    if (tempMessage != null)
                    {
                        tempMessage.date = convertTimespanToDate(tempMessage.timespan)
                        it.messages.add(tempMessage)
                    }
                }
            }
        }
    }

    fun loadChatRooms()
    {
        chatRooms.value = arrayListOf()
        database.collection(CHATROOM_PREFIX).get().addOnSuccessListener { it ->
            it.forEach{
                val tempChatRoom = it.toObject(ChatRoom::class.java)
                tempChatRoom.key = it.id
                chatRooms.value?.add(tempChatRoom)
            }
        }
            .addOnSuccessListener {
            loadLatestMessageFromRooms()
        }
    }


    private fun predefinedChatRooms()
    {

        val chatRoom = ChatRoom("First chat room", "This is a chat room with stuff")
        val chatRoom2 = ChatRoom( "Second chat room", "This is a chat room with stuff")
        val chatRoom3 = ChatRoom( "Third chat room", "This is a chat room with stuff")

        database.document("$CHATROOM_PREFIX/4499b251-faf8-45f7-9f2e-a83a0f35fb50").set(chatRoom)
        database.document("$CHATROOM_PREFIX/bc34617e-fe22-468f-993d-8b202fca7788").set(chatRoom2)
        database.document("$CHATROOM_PREFIX/f60c7c5f-e2ed-46b0-acbe-f2eee9e4bacb").set(chatRoom3)

        //removes in the final commit TODO(REMOVE)

        val message1 = Message("test 1", User("", "its me"), getCurrentTimeSpan(1000))
        val message2 = Message("test 2", User("", "its me"), getCurrentTimeSpan(0))
        val message3 = Message("test 3", User("", "its me"), getCurrentTimeSpan(0))

        database.document("$CHATROOM_PREFIX/4499b251-faf8-45f7-9f2e-a83a0f35fb50/messages/c9f92cd5-88ed-4aee-9c9a-0fa4c4d243e4").set(message1)
        database.document("$CHATROOM_PREFIX/bc34617e-fe22-468f-993d-8b202fca7788/messages/18e8d9f7-e5e5-4703-95a8-60ac98656a67").set(message2)
        database.document("$CHATROOM_PREFIX/f60c7c5f-e2ed-46b0-acbe-f2eee9e4bacb/messages/8164af31-b986-4bac-9ef5-01b04f8d921c").set(message3)

    }

    private fun getCurrentTimeSpan(fakeTime: Long) : Long
    {
        val timeStamp: Long = (System.currentTimeMillis() + fakeTime)
        return timeStamp

    }

    private fun convertTimespanToDate(timestamp: Long) : Date
    {
        val ts = Timestamp(timestamp)
        return Date(ts.time)
    }

    private fun imageToString(image : Bitmap) : String
    {
        val byteOutStream = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.PNG, 100, byteOutStream)
        val b: ByteArray = byteOutStream.toByteArray()
        return Base64.encodeToString(b, Base64.DEFAULT)
    }





}