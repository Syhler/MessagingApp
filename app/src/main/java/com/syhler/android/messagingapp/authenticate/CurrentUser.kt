package com.syhler.android.messagingapp.authenticate

import android.graphics.Bitmap
import android.util.Log
import com.facebook.Profile
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.iid.FirebaseInstanceId
import com.syhler.android.messagingapp.R
import com.syhler.android.messagingapp.authenticate.enums.AuthenticationMethod
import com.syhler.android.messagingapp.utillities.BitmapManipulation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class CurrentUser
{

    var fullName : String? = ""
    var image : Bitmap? = null
    lateinit var authenticationMethod: AuthenticationMethod
    private var photoUrl : String = ""
    var chatRoomKey : String = ""
    var authenticationID : String = ""
    var isloggedIn : Boolean = false
    var deviceId : String = ""

    init {
        loadUserData()
        loadProfilePicture()
        getDeviceId()
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
            return BitmapManipulation.toByte(image!!)
        }
        return ""
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
        photoUrl = user.linkUri.toString()
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

    private fun getDeviceId()
    {
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    //Log.w(TAG, "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new Instance ID token
                deviceId = task.result?.token!!
                //Log.d(TAG, msg)
            })
    }




}