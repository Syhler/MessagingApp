package com.syhler.android.messagingapp.ui.chatroom.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.syhler.android.messagingapp.R
import com.syhler.android.messagingapp.authenticate.CurrentUser
import com.syhler.android.messagingapp.data.entites.Message

class MessageRecycleViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>()
{

    var messages : MutableList<Message> = arrayListOf()

    fun submitMessages(value : List<Message>) : Int
    {
        val countBeforeAdd = messages.size
        messages.clear()
        messages.addAll(value)
        notifyDataSetChanged()
        return countBeforeAdd
    }


    fun add(value : Message)
    {
        messages.add(value)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val incomingMessageView = LayoutInflater.from(parent.context).inflate(R.layout.incoming_message, parent, false)
        val currentUserMessageView = LayoutInflater.from(parent.context).inflate(R.layout.current_user_message, parent, false)

        when(viewType)
        {
            0-> return CurrentUserMessageViewHolder(currentUserMessageView)
            1 -> return IncomingMessageViewHolder(incomingMessageView)
        }

        //not sure what to do here
        return IncomingMessageViewHolder(currentUserMessageView)
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int)
    {

        when(holder)
        {
            is CurrentUserMessageViewHolder ->
            {
                holder.bind(messages[ holder.adapterPosition])
            }
            is IncomingMessageViewHolder ->
            {
                holder.bind(messages[ holder.adapterPosition])
            }
        }

    }

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].isBelongToCurrentUser(CurrentUser.getInstance())) {
            0
        } else{
            1
        }
    }

    override fun getItemCount(): Int
    {
        return messages.size
    }

}



