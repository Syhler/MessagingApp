package com.syhler.android.messagingapp.ui.chatroom.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.syhler.android.messagingapp.R
import com.syhler.android.messagingapp.data.entites.Message

class CurrentUserMessageViewHolder constructor(itemView : View) : RecyclerView.ViewHolder(itemView)
{
    private val image : ImageView = itemView.findViewById(R.id.message_image)
    private val messageBody : TextView = itemView.findViewById(R.id.message_body)

    fun bind(message: Message)
    {
        if (!message.imageUri.isBlank())
        {
            displayImage(message.imageUri)
        }
        else
        {
            image.setImageResource(0)
            messageBody.visibility = View.VISIBLE
            messageBody.text = message.text
        }
    }

    private fun displayImage(imageUri : String)
    {
        try {
            val ref = FirebaseStorage.getInstance().getReferenceFromUrl(imageUri)
            Glide.with(itemView.context)
                .load(ref)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_foreground)
                .override(500,500)
                .dontAnimate()
                .into(image)
        }catch (e : IllegalArgumentException) {
            Glide.with(itemView.context)
                .load(imageUri)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_foreground)
                .override(500,500)
                .dontAnimate()
                .into(image)
        }
        messageBody.visibility = View.GONE
    }


}