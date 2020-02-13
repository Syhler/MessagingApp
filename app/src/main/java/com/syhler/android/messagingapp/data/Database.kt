package com.syhler.android.messagingapp.data

import android.content.Context
import android.graphics.Bitmap
import android.util.Base64
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.*
import com.syhler.android.messagingapp.data.entites.ChatRoom
import com.syhler.android.messagingapp.data.entites.Message
import com.syhler.android.messagingapp.data.entites.User
import java.io.ByteArrayOutputStream
import kotlin.collections.HashMap


class Database
{
    private val database : DatabaseReference = FirebaseDatabase.getInstance().reference.root

    var chatRooms : MutableLiveData<MutableList<ChatRoom>> = MutableLiveData()

    init {
        chatRooms.value = mutableListOf()
    }


    companion object
    {
        @Volatile private var instance : Database? = null

        fun getInstance() = instance ?: synchronized(this)
        {
            instance ?: Database()
        }
    }

    fun setup()
    {
        predefinedChatRooms()
        loadData()
    }

    fun loadData()
    {
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot)
            {
                for (rooms in dataSnapshot.children) {
                    val roomObject = rooms.getValue(ChatRoom::class.java)!!
                    chatRooms.value?.add(roomObject)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        }
        database.addValueEventListener(postListener)
    }

    private fun predefinedChatRooms()
    {

        //creates predefined chat rooms
        val chatRoom = ChatRoom(arrayListOf(Message("test", User("", "its me"), "")), "First chat room", "This is a chat room with stuff")
        val chatRoom2 = ChatRoom(arrayListOf(), "Second chat room", "This is a chat room with stuff")
        val chatRoom3 = ChatRoom(arrayListOf(), "Third chat room", "This is a chat room with stuff")

        val map = HashMap<String, Any>()
        map.put("1",chatRoom)
        map.put("2",chatRoom2)
        map.put("3",chatRoom3)

        database.updateChildren(map)
    }

    private fun imageToString(image : Bitmap) : String
    {
        val byteOutStream = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.PNG, 100, byteOutStream)
        val b: ByteArray = byteOutStream.toByteArray()
        return Base64.encodeToString(b, Base64.DEFAULT)
    }





}