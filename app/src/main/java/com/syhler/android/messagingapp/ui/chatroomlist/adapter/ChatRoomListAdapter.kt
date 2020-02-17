package com.syhler.android.messagingapp.ui.chatroomlist.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.syhler.android.messagingapp.R
import com.syhler.android.messagingapp.data.entites.ChatRoom

class ChatRoomListAdapter(context: Context) : BaseAdapter()
{

    private val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private lateinit var row : View
    private val chatRooms : MutableList<ChatRoom> = mutableListOf()


    fun addAll(value : List<ChatRoom>)
    {
        chatRooms.addAll(value)
        notifyDataSetChanged()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View
    {

        val rowView = layoutInflater.inflate(R.layout.room_row, parent, false)


        val title = rowView.findViewById<TextView>(R.id.row_title)
        val description = rowView.findViewById<TextView>(R.id.row_description)

        val chatRoom = getItem(position) as ChatRoom

        title.text = chatRoom.name
        if (chatRoom.messages.size > 0)
        {
            description.text = chatRoom.description

        }
        else{
            description.text = chatRoom.description
        }



        return rowView

    }

    override fun getItem(position: Int): Any
    {
        return chatRooms[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int
    {
        return chatRooms.size
    }
}