package com.syhler.android.messagingapp.notification

import android.content.Context
import android.util.Log
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.syhler.android.messagingapp.authenticate.CurrentUser
import com.syhler.android.messagingapp.data.entites.Message
import com.syhler.android.messagingapp.utillities.KeyFields
import org.json.JSONException
import org.json.JSONObject


class SendingNotification(context: Context)
{
    private val TAG = "SendingNotification"

    private val fcmAPI : String = "https://fcm.googleapis.com/fcm/send"
    private val serverKey = "key=AAAA1tzocBE:APA91bFT_hVsC1KnILNEdC1ZsjuMgKUkbYA81bjPi19ce38-Ul-6_x8waygw7N79cfn67Zbf8Mep79LGNQdsr8VnvOftnjVLSyOjF-qXYTOEVUUVC54KmXkRmT2zQpCp7_N0q-KebJGW"
    private val contentType = "application/json"

    private val requestQueue: RequestQueue by lazy {
        Volley.newRequestQueue(context)
    }

    fun sendNotification(chatRoomKey : String?, message : Message, chatRoomName : String?)
    {
        if (chatRoomKey == null || chatRoomKey.isBlank())
        {
            Log.w(TAG, "topic name doesn't exist")
            return
        }

        val topicName = "/topics/$chatRoomKey" //topic has to match what the receiver subscribed to

        val notification = JSONObject()
        val notificationBody = JSONObject()

        try {
            notificationBody.put("title", "New message from ${message.messageUser.fullName} : $chatRoomName")
            notificationBody.put("message", message.text)
            notificationBody.put(KeyFields.chatRoomKey, chatRoomKey)
            notificationBody.put(KeyFields.chatRoomName, chatRoomName)
            notificationBody.put(KeyFields.currentUserAuth, CurrentUser.getInstance().authenticationID)

            notification.put("to", topicName)
            notification.put("data", notificationBody)
        } catch (e: JSONException) {
            Log.e(TAG, "onCreate: " + e.message)
        }

        sendNotification(notification)
    }


    private fun sendNotification(notification: JSONObject) {
        val jsonObjectRequest = object : JsonObjectRequest(fcmAPI, notification,
            Response.Listener<JSONObject> { response ->
                Log.i(TAG, "onResponse: $response")
            },
            Response.ErrorListener {
                Log.e(TAG, "onErrorResponse: Something went wrong", it)
            }) {

            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                params["Authorization"] = serverKey
                params["Content-Type"] = contentType
                return params
            }
        }

        requestQueue.add(jsonObjectRequest)
    }





}