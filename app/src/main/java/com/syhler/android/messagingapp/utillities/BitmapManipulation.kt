package com.syhler.android.messagingapp.utillities

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.io.ByteArrayOutputStream

class BitmapManipulation
{
    companion object
    {
        fun toByte(image : Bitmap) : String
        {
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

        fun fromPath(path : String) : Bitmap
        {
            return BitmapFactory.decodeFile(path)
        }
    }
}