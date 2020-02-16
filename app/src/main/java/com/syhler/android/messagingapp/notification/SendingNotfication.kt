package com.syhler.android.messagingapp.notification

import android.content.Context
import android.util.Log
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject

class SendingNotfication(context: Context)
{
    private val fcmAPI : String = "https://fcm.googleapis.com/fcm/send"
    private val serverKey = "key=AAAA1tzocBE:APA91bFT_hVsC1KnILNEdC1ZsjuMgKUkbYA81bjPi19ce38-Ul-6_x8waygw7N79cfn67Zbf8Mep79LGNQdsr8VnvOftnjVLSyOjF-qXYTOEVUUVC54KmXkRmT2zQpCp7_N0q-KebJGW"
    private val contentType = "application/json"

    private val requestQueue: RequestQueue by lazy {
        Volley.newRequestQueue(context)
    }

    fun sendNotfication(topicName : String, message : String)
    {
        val topic = "/topics/" + topicName //topic has to match what the receiver subscribed to

        val notification = JSONObject()
        val notificationBody = JSONObject()

        try {
            notificationBody.put("title", "Enter_title")
            notificationBody.put("message", message)   //Enter your notification message
            notification.put("to", topic)
            notification.put("data", notificationBody)
            Log.e("TAG", "try")
        } catch (e: JSONException) {
            Log.e("TAG", "onCreate: " + e.message)
        }

        sendNotification(notification)
    }


    private fun sendNotification(notification: JSONObject) {
        Log.e("TAG", "sendNotification")
        val jsonObjectRequest = object : JsonObjectRequest(fcmAPI, notification,
            Response.Listener<JSONObject> { response ->
                Log.i("TAG", "onResponse: $response")

                //respone?

                //msg.setText("")
            },
            Response.ErrorListener {
                //Toast.makeText(this@MainActivity, "Request error", Toast.LENGTH_LONG).show()
                Log.i("TAG", "onErrorResponse: Didn't work")
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