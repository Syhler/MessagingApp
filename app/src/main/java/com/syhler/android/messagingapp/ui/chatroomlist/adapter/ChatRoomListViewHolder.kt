package com.syhler.android.messagingapp.ui.chatroomlist.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.syhler.android.messagingapp.R
import com.syhler.android.messagingapp.data.entites.ChatRoom

class ChatRoomListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
{

    private val image = itemView.findViewById<ImageView>(R.id.chatRoom_row_image)
    private val title = itemView.findViewById<TextView>(R.id.chatRoom_row_title)
    private val description = itemView.findViewById<TextView>(R.id.chatRoom_row_description)

    fun bind(chatRoom : ChatRoom)
    {

        if (!chatRoom.image.isBlank())
        {
            Glide.with(itemView.context)
                .load(chatRoom.image)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(image)
        }

        title.text = chatRoom.title
        description.text = chatRoom.description
    }
}