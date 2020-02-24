package com.syhler.android.messagingapp.data.repos

import android.net.Uri
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.syhler.android.messagingapp.data.entites.UserNotificationStatus
import java.lang.Exception

class UserRepository(authenticationId : String)
{
    private val userCollectionPath = "users/$authenticationId"
    private val storagePath = "users/$authenticationId"

    private val database : FirebaseFirestore = FirebaseFirestore.getInstance()
    private val storage : FirebaseStorage = FirebaseStorage.getInstance()

    fun addNotificationStatusForRoom(activate : Boolean,chatRoomKey: String)
    {
        database.document("$userCollectionPath/chat_rooms/$chatRoomKey").set(UserNotificationStatus(activate, chatRoomKey))
    }

    fun getNotificationStatusForAllRooms() : CollectionReference?
    {
        return try {
            database.collection("$userCollectionPath/chat_rooms")

        }catch (e : Exception) {
            null
        }

    }

    fun addPicture(imageUri : Uri)
    {
        val ref =  storage.getReference(storagePath).child("dd.jpg")

        ref.putFile(imageUri).addOnFailureListener {
            val e = it
        }
    }
}