package com.syhler.android.messagingapp.ui.chatroomlist.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.syhler.android.messagingapp.R
import com.syhler.android.messagingapp.data.entites.ChatRoom

class ChatRoomListRecyclerAdapter(private val onClickListener: View.OnClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>()
{
    var chatRooms : List<ChatRoom> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
    {
        val view =  LayoutInflater.from(parent.context).inflate(R.layout.room_row, parent, false)
        view.setOnClickListener(onClickListener)
        return ChatRoomListViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int)
    {

        if (holder is ChatRoomListViewHolder)
        {
            holder.bind(chatRooms[position])
        }
    }

    override fun getItemCount(): Int
    {
        return chatRooms.size
    }

    fun submitList(value : List<ChatRoom>)
    {
        chatRooms = value
        notifyDataSetChanged()
    }

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
}