package com.syhler.android.messagingapp.ui.chatroom

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import com.syhler.android.messagingapp.R
import com.syhler.android.messagingapp.data.entites.ChatRoom

class ChatRoomAdapter(context: Context, val chatRooms: MutableLiveData<MutableList<ChatRoom>>) : BaseAdapter()
{

    val layoutInflater = context.applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private lateinit var row : View

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View
    {

        val rowView = layoutInflater.inflate(R.layout.room_row, parent, false)


        val title = rowView.findViewById<TextView>(R.id.row_title)
        val description = rowView.findViewById<TextView>(R.id.row_description)

        val chatRoom = getItem(position) as ChatRoom

        title.text = chatRoom.name
        description.text = chatRoom.description



        return rowView

    }

    override fun getItem(position: Int): Any
    {
        return chatRooms.value?.get(position)!!
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int
    {
        return if (chatRooms.value == null) {
            5
        } else{
            chatRooms.value!!.size
        }
    }
}