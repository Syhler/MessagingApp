package com.syhler.android.messagingapp.authenticate

import android.graphics.Bitmap
import com.facebook.Profile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.syhler.android.messagingapp.authenticate.enums.AuthenticationMethod
import com.syhler.android.messagingapp.data.entites.UserNotificationStatus
import com.syhler.android.messagingapp.data.repos.UserRepository
import com.syhler.android.messagingapp.utillities.BitmapManipulation
import com.syhler.android.messagingapp.utillities.KeyFields.chatRoomKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class CurrentUser
{

    var fullName : String? = ""
    var image : Bitmap? = null
    lateinit var authenticationMethod: AuthenticationMethod
    var currentChatRoomKey : String = ""
    var authenticationID : String = ""
    var isloggedIn : Boolean = false

    private var photoUrl : String = ""
    private val userRepository : UserRepository
    private val notificationStatusForRooms : MutableList<UserNotificationStatus> = mutableListOf()


    init {
        loadUserData()
        userRepository = UserRepository(authenticationID)
        loadNotificationStatus()
        loadProfilePicture()
    }


    companion object
    {
        private var instance : CurrentUser? = null

        fun getInstance() : CurrentUser
        {
           return if (instance == null) {
                instance = CurrentUser()
                instance!!
            } else{
                instance!!
            }
        }

        fun initialize()
        {
            instance = CurrentUser()
        }
    }

    fun getImageAsByte(): String?
    {
        if (image != null)
        {
            return BitmapManipulation.toBase64(image!!)
        }
        return ""
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

    //save it to disk and only load from internet if picture not already found in db
    private fun loadProfilePicture()
    {
        if (image == null)
        {
            getBitmapFromURL(photoUrl)
        }
    }


    private fun getBitmapFromURL(src: String?) : Job {
        return CoroutineScope(IO).launch {
            val bitmap = BitmapManipulation.getFromURL(src)
            if (bitmap != null)
            {
                image = bitmap
            }
        }
    }

}