package com.syhler.android.messagingapp.ui.chatroom.adapter

import android.annotation.SuppressLint
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.syhler.android.messagingapp.R
import com.syhler.android.messagingapp.data.entites.Message
import com.syhler.android.messagingapp.utillities.BitmapManipulation
import java.text.SimpleDateFormat
import java.util.*

class IncomingMessageViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView)
{
    private val image : ImageView = itemView.findViewById(R.id.message_image)
    private val avatar : ImageView = itemView.findViewById(R.id.message_avatar)
    private val name : TextView = itemView.findViewById(R.id.message_owner_name)
    private val messageBody : TextView = itemView.findViewById(R.id.message_body)

    @SuppressLint("SetTextI18n")
    fun bind(message: Message)
    {
        name.text = message.messageUser.fullName + " - " + getTimeNow(message.date)
        setIncomingPicture(message.messageUser.image)


        if (!message.imageUri.isBlank())
        {

            val ref = FirebaseStorage.getInstance().getReferenceFromUrl(message.imageUri)
            Glide.with(itemView.context)
                .load(ref)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_foreground)
                .override(500,500)
                .dontAnimate()
                .into(image)
            messageBody.visibility = View.GONE

        }
        else
        {
            image.setImageResource(0)
            messageBody.visibility = View.VISIBLE
            messageBody.text = message.text
        }
    }

    //TODO(update so that the image get saved in firebase)
    private fun setIncomingPicture(imageBinary: String?)
    {
        val image =
            BitmapManipulation.getFromByte(imageBinary)
        if (image != null)
        {
            avatar.setImageBitmap(image)
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun getTimeNow(date : Date) : String
    {
        val calendar = Calendar.getInstance()
        calendar.time = date
        val dateFormatter = SimpleDateFormat("HH:mm")
        return dateFormatter.format(date)
    }
}