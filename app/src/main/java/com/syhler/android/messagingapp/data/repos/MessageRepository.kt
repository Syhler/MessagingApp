package com.syhler.android.messagingapp.data.repos

import android.content.ContentResolver
import android.net.Uri
import android.webkit.MimeTypeMap
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FileDownloadTask
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.syhler.android.messagingapp.data.entites.Message
import java.util.*

class MessageRepository(chatRoomKey : String, private var contentResolver: ContentResolver)
{
    private val CHATROOM_PREFIX = "chat_rooms"
    private val messageCollectionPath = "$CHATROOM_PREFIX/$chatRoomKey/messages"
    private val storagePath = "$CHATROOM_PREFIX/$chatRoomKey"

    private var database = FirebaseFirestore.getInstance()
    private var storage = FirebaseStorage.getInstance().getReference(storagePath)

    fun getInitMessages(numberOfMessages : Long) : Query
    {
        return database.collection(messageCollectionPath)
            .orderBy("timespan")
            .limitToLast(numberOfMessages)
    }

    fun getLatestMessage(fromTimeStamp: Long): Query
    {
        return database.collection(messageCollectionPath)
            .orderBy("timespan")
            .whereGreaterThan("timespan", fromTimeStamp)
            .limitToLast(1)
    }

    fun getMessageFrom(fromTimeStamp : Long, limit : Long) : Query
    {
        return database.collection(messageCollectionPath)
            .orderBy("timespan")
            .whereLessThan("timespan", fromTimeStamp)
            .limitToLast(limit)
    }

    fun getImage(imageUri: Uri) : FileDownloadTask
    {
        return storage.getFile(imageUri)
    }

    fun uploadImage(imageUri : Uri) : UploadTask
    {
        val fileReference = storage.child(
            System.currentTimeMillis().toString() + "." + getFileExtension(imageUri))

        return fileReference.putFile(imageUri)
    }

    fun addMessage(message: Message)
    {
        database.collection(messageCollectionPath).document(UUID.randomUUID().toString())
            .set(message)
    }

    private fun getFileExtension(uri: Uri): String? {
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(contentResolver.getType(uri))
    }




}