package com.syhler.android.messagingapp.utillities

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

object BitmapManipulation
{

    fun toByte(image: Bitmap?) : String
    {
        if (image == null) return ""

        val byteOutStream = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.PNG, 100, byteOutStream)
        val b: ByteArray = byteOutStream.toByteArray()
        return Base64.encodeToString(b, Base64.DEFAULT)
    }

    fun getResized(image: Bitmap, maxSize: Int): Bitmap? {
        var width = image.width
        var height = image.height
        val bitmapRatio = width.toFloat() / height.toFloat()
        if (bitmapRatio > 0) {
            width = maxSize
            height = (width / bitmapRatio).toInt()
        } else {
            height = maxSize
            width = (height * bitmapRatio).toInt()
        }
        return Bitmap.createScaledBitmap(image, width, height, true)
    }

    fun toUriConverter(mBitmap: Bitmap?, context: Context): Uri? {
        var uri: Uri? = null
        try {
            val options = BitmapFactory.Options()
            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false

            val file = File(context.filesDir, (System.currentTimeMillis().toString() + ".jpeg"))

            val out: FileOutputStream = context.openFileOutput(file.name, Context.MODE_APPEND)
            mBitmap?.compress(Bitmap.CompressFormat.JPEG, 100, out)
            out.flush()
            out.close()
            //get absolute path
            val realPath: String = file.absolutePath
            val f = File(realPath)
            uri = Uri.fromFile(f)
        } catch (e: Exception) {
            Log.e("Your Error Message", e.message)
        }
        return uri
    }

    fun getFromURL(src: String?) : Bitmap?
    {
        return try {
            val url = URL(src)
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            BitmapFactory.decodeStream(connection.inputStream)
        } catch (e: IOException) {
            Log.w("BitmapManipulation", "converting url to bitmap failed", e)
            null
        }
    }

    fun getFromByte(image : String?) : Bitmap?
    {
        return try {
            val encodeByte: ByteArray = Base64.decode(image, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.size)
        } catch (e: Exception) {
            null
        }
    }
}