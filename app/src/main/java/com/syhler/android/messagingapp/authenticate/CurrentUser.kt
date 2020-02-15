package com.syhler.android.messagingapp.authenticate

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import com.facebook.Profile
import com.google.firebase.auth.FirebaseAuth
import com.syhler.android.messagingapp.authenticate.enums.AuthenticationMethod
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL


class CurrentUser
{

    var fullName : String? = ""
    lateinit var image : Bitmap
    lateinit var authenticationMethod: AuthenticationMethod
    private lateinit var photoUrl : String
    var chatRoomKey : String = ""
    var authenticationID : String = ""

    init {
        loadUserData()
    }


    companion object
    {
        private var instance : CurrentUser? = null

        fun getInstace() : CurrentUser
        {
            return if (instance == null) {
                instance = CurrentUser()
                instance!!
            } else{
                instance!!
            }
        }
    }


    fun imageToString() : String
    {
        val byteOutStream = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.PNG, 100, byteOutStream)
        val b: ByteArray = byteOutStream.toByteArray()
        return Base64.encodeToString(b, Base64.DEFAULT)
    }


    private fun loadUserData()
    {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val facebookCurrentUser = Profile.getCurrentProfile()

        if  (currentUser!= null)
        {
            fullName = currentUser.displayName
            authenticationMethod = AuthenticationMethod.GOOGLE
            photoUrl = currentUser.photoUrl.toString()
            authenticationID = currentUser.uid
        }
        else if (facebookCurrentUser != null)
        {
            fullName = facebookCurrentUser.name
            authenticationMethod = AuthenticationMethod.FACEBOOK
            photoUrl = facebookCurrentUser.getProfilePictureUri(100,100).toString()
            authenticationID = facebookCurrentUser.id
        }

    }

    //save it to disk and only load from internet if picture not already found in db
    fun getProfileImage() : Job
    {
        return getBitmapFromURL(photoUrl)
    }


    private fun getBitmapFromURL(src: String?) : Job {
        return CoroutineScope(IO).launch {
            try {
                val url = URL(src)
                val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
                connection.doInput = true
                connection.connect()
                val input: InputStream = connection.getInputStream()
                image = BitmapFactory.decodeStream(input)
            } catch (e: IOException) {
                e.printStackTrace()
                //show toast
                //TODO("show toast")
            }
        }

    }


}