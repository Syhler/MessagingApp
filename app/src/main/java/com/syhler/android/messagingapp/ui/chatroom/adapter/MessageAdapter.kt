package com.syhler.android.messagingapp.ui.chatroom.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.syhler.android.messagingapp.R
import com.syhler.android.messagingapp.authenticate.CurrentUser
import com.syhler.android.messagingapp.data.entites.Message
import com.syhler.android.messagingapp.utillities.BitmapManipulation
import java.text.SimpleDateFormat
import java.util.*


class MessageAdapter(context: Context) : BaseAdapter()
{

    private val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    private var holder = MessageViewHolder()

    private var messages : MutableList<Message> = arrayListOf()

    fun init(message : List<Message>) : Int
    {
        val countBeforeAdd = messages.size
        messages.clear()
        messages.addAll(message)
        notifyDataSetChanged()
        return countBeforeAdd
    }

    fun add(message: Message)
    {
        messages.add(message)
        notifyDataSetChanged()
    }



    fun addAllAtStart(message : List<Message>)
    {
        messages.addAll(0, message)
        notifyDataSetChanged()
    }



    @SuppressLint("SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View
    {
        holder = MessageViewHolder()
        val message = messages[position]
        val view : View

        if (message.isBelongToCurrentUser(CurrentUser.getInstance()))
        {
            view = setupViewHolderCurrentUser()

            view.tag = holder

            if (messageContainsImage(message.image))
            {
                displayImage(message.image)
            }
            else
            {
                holder.messageBody.text = message.text
            }
        }
        else
        {
            view =  setupViewHolderIncomingMessage()

            view.tag = holder

            holder.name.text = message.messageUser.fullName +" - " + getTimeNow(message.date)
            setIncomingPicture(message.messageUser.image)

            if (messageContainsImage(message.image))
            {
                displayImage(message.image)
            }
            else
            {
                holder.messageBody.text = message.text

            }
        }
        return view
    }

    private fun messageContainsImage(imageBinary: String)  = !imageBinary.isBlank()


    private fun setupViewHolderCurrentUser() : View
    {
        val view = layoutInflater.inflate(R.layout.current_user_message, null)
        holder.messageBody = view.findViewById(R.id.message_body)
        holder.image = view.findViewById(R.id.message_image)
        return view
    }

    private fun setupViewHolderIncomingMessage() : View
    {
        val view = layoutInflater.inflate(R.layout.incoming_message, null)
        holder.avatar = view.findViewById(R.id.message_avatar)
        holder.name = view.findViewById(R.id.message_owner_name)
        holder.messageBody = view.findViewById(R.id.message_body)
        holder.image = view.findViewById(R.id.message_image)
        return view
    }

    private fun setIncomingPicture(imageBinary: String?)
    {
        val image = BitmapManipulation.getFromByte(imageBinary)
        if (image != null)
        {
            holder.avatar.setImageBitmap(image)
        }
    }

    private fun displayImage(imageBinary : String)
    {
        val image = BitmapManipulation.getFromByte(imageBinary)
        holder.messageBody.visibility = View.GONE
        holder.image.setImageBitmap(image)
    }

    @SuppressLint("SimpleDateFormat")
    private fun getTimeNow(date : Date) : String
    {
        val calendar = Calendar.getInstance()
        calendar.time = date
        val dateFormatter = SimpleDateFormat("HH:mm")
        return dateFormatter.format(date)
    }

    override fun getItem(position: Int): Any {
        return messages[position]

    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int
    {
        return messages.size
    }



    class MessageViewHolder
    {
        lateinit var image : ImageView
        lateinit var avatar: ImageView
        lateinit var name: TextView
        lateinit var messageBody: TextView
    }
}


