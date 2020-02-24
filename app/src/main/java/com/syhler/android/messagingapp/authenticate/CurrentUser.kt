package com.syhler.android.messagingapp.authenticate

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.facebook.Profile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.syhler.android.messagingapp.authenticate.enums.AuthenticationMethod
import com.syhler.android.messagingapp.data.entites.UserNotificationStatus
import com.syhler.android.messagingapp.data.repos.UserRepository
import com.syhler.android.messagingapp.utillities.BitmapManipulation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class CurrentUser(private val context: Context)
{

    var fullName : String? = ""
    lateinit var authenticationMethod: AuthenticationMethod
    var currentChatRoomKey : String = ""
    var authenticationID : String = ""
    var isloggedIn : Boolean = false
    var imageUri : Uri = Uri.EMPTY


    private var tempImage : Bitmap? = null
    private var photoUrl : String = ""
    private val userRepository : UserRepository
    private val notificationStatusForRooms : MutableList<UserNotificationStatus> = mutableListOf()


    init {
        loadUserData()
        userRepository = UserRepository(authenticationID)
        userRepository.getPictureForUser()
        loadNotificationStatus()
        loadProfilePicture()
    }


    companion object
    {
        private var instance : CurrentUser? = null

        fun getInstance() : CurrentUser
        {
           return if (instance == null) {
               throw Exception("Not instantiated - please initialize object first")
            } else{
                instance!!
            }
        }

        fun initialize(context: Context) : CurrentUser
        {
            instance = CurrentUser(context)
            return instance!!
        }

        fun destroy()
        {
            instance = null
        }
    }


    fun addNotificationStatus(activate : Boolean, chatRoomKey: String)
    {
        userRepository.addNotificationStatusForRoom(activate, chatRoomKey)
        notificationStatusForRooms.add(UserNotificationStatus(true, chatRoomKey))
    }

    fun getNotificationStatusFrom(chatRoomKey : String?) : UserNotificationStatus?
    {
        notificationStatusForRooms.forEach {
            if (it.chatRoomKey == chatRoomKey)
            {
                return it
            }
        }

        return null
    }


    private fun loadNotificationStatus()
    {
        val collection = userRepository.getNotificationStatusForAllRooms()

        collection?.get()?.addOnSuccessListener { userCollection ->

            for (doc in userCollection)
            {
                val notificationStatus = doc?.toObject(UserNotificationStatus::class.java)
                if (notificationStatus != null)
                {
                    notificationStatusForRooms.add(notificationStatus)
                }
            }
        }
    }

    private fun loadUserData()
    {
        val googleCurrentUser = FirebaseAuth.getInstance().currentUser
        val facebookCurrentUser = Profile.getCurrentProfile()

        if  (googleCurrentUser!= null)
        {
            setupGoogleUser(googleCurrentUser)
        }
        else if (facebookCurrentUser != null)
        {
            setupFacebookUser(facebookCurrentUser)
        }

    }

    private fun setupGoogleUser(user : FirebaseUser)
    {
        fullName = user.displayName
        authenticationMethod = AuthenticationMethod.GOOGLE
        photoUrl = user.photoUrl.toString()
        authenticationID = user.uid
        isloggedIn = true
    }

    private fun setupFacebookUser(user : Profile)
    {
        fullName = user.name
        authenticationMethod = AuthenticationMethod.FACEBOOK
        photoUrl = user.getProfilePictureUri(100,100).toString()
        authenticationID = user.id
        isloggedIn = true
    }


    private fun loadProfilePicture()
    {
        if (tempImage == null)
        {
            val ref =userRepository.getProfilePictureReference()
            ref.downloadUrl
                .addOnSuccessListener {
                    imageUri = it
                }
                .addOnFailureListener {
                    uploadImage(ref)
                }

        }
    }

    private fun uploadImage(ref : StorageReference)
    {
        getBitmapFromURL(photoUrl).invokeOnCompletion {
            val uri = BitmapManipulation.toUriConverter(tempImage, context)
            if (uri != null)
            {
                val uploadTask = ref.putFile(uri)
                getNewlyaddedImageUrl(uploadTask)
            }
        }
    }

    private fun getNewlyaddedImageUrl(uploadTask: UploadTask)
    {
        uploadTask.addOnSuccessListener{taskSnapshot: UploadTask.TaskSnapshot? ->

            val uploadedFileUri = taskSnapshot?.uploadSessionUri
            if (uploadedFileUri != null)
            {
                imageUri = uploadedFileUri
            }

        }
    }

    private fun getBitmapFromURL(src: String?) : Job {
        return CoroutineScope(IO).launch {
            val bitmap = BitmapManipulation.getFromURL(src)
            if (bitmap != null)
            {
                tempImage = bitmap
            }
        }
    }

}