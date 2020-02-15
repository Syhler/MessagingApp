package com.syhler.android.messagingapp.ui.chatroomlist.chatroom.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.syhler.android.messagingapp.R
import com.syhler.android.messagingapp.authenticate.CurrentUser
import com.syhler.android.messagingapp.data.entites.Message
import java.text.SimpleDateFormat
import java.util.*


class MessageAdapter(context: Context) : BaseAdapter()
{

    private val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    private var holder =
        MessageViewHolder()
    //private val currentUser = CurrentUser()
    private val messages : MutableList<Message> = arrayListOf()

    fun addAll(message : List<Message>)
    {
        messages.addAll(message)
        notifyDataSetChanged()
    }

    fun add(message: Message)
    {
        messages.add(message)
        notifyDataSetChanged()
    }



    @SuppressLint("SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View
    {
        holder = MessageViewHolder()
        val message = messages[position]
        val view : View

        if (message.isBelongToCurrentUser(CurrentUser.getInstace()))
        {
            view = setupViewHolderCurrentUser()

            view.tag = holder

            if (messageContainsImage(message.image)) {
                displayImage(message.image)
            }

            holder.messageBody.text = message.text
        }
        else
        {
            view =  setupViewHolderIncomingMessage()

            view.tag = holder

            if (messageContainsImage(message.image))
            {
                displayImage(message.image)
            }

            setIncomingPicture(message.user.image)

            holder.name.text = message.user.fullName +" - " + getTimeNow(message.date)
            holder.messageBody.text = message.text

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
        val image = getImageAsBitmapFromString(imageBinary)
        if (image != null)
        {
            holder.avatar.setImageBitmap(image)
        }
    }

    private fun displayImage(imageBinary : String)
    {
        val image = getImageAsBitmapFromString(imageBinary)
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

    private fun getImageAsBitmapFromString(image : String?) : Bitmap?
    {
        return try {
            val encodeByte: ByteArray = Base64.decode(image, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.size)
        } catch (e: Exception) {
            null
        }
    }

    class MessageViewHolder
    {
        lateinit var image : ImageView
        lateinit var avatar: ImageView
        lateinit var name: TextView
        lateinit var messageBody: TextView
    }
}


