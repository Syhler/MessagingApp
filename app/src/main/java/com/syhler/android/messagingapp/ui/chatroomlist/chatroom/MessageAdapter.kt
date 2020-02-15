package com.syhler.android.messagingapp.ui.chatroomlist.chatroom

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CalendarView
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



    @SuppressLint("SimpleDateFormat")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View
    {
        val holder = MessageViewHolder()
        val messageInflater = layoutInflater
        val message = messages[position]
        var view : View? = null

        if (message.isBelongToCurrentUser(CurrentUser.getInstace()))
        {
            view = messageInflater.inflate(R.layout.current_user_message, null)
            holder.messageBody = view.findViewById(R.id.message_body)
            view.tag = holder
            holder.messageBody.text = message.text
        }
        else
        {
            view = messageInflater.inflate(R.layout.incoming_message, null)
            holder.avatar = view.findViewById(R.id.message_avatar)
            holder.name = view.findViewById(R.id.message_owner_name)
            holder.messageBody = view.findViewById(R.id.message_body)
            holder.image = view.findViewById(R.id.message_image)
            holder.date = view.findViewById(R.id.message_date)
            view.tag = holder

            var calendar = Calendar.getInstance()
            calendar.time = message.date
            var dateFormatter = SimpleDateFormat("HH:mm")
            var dateString = dateFormatter.format(message.date)

            if (!message.image.isBlank())
            {
                val image = getImageAsBitmap(message.image)
                holder.messageBody.visibility = View.GONE
                holder.image.setImageBitmap(image)
            }

            //holder.date.text = dateFormatter.format(message.date)
            holder.name.text = message.user.fullName +" - " + dateString
            holder.messageBody.text = message.text
            val image = getImageAsBitmap(message.user.image)
            if (image != null)
            {
                holder.avatar.setImageBitmap(image)
            }
        }

        return view
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

    fun getImageAsBitmap(image : String?) : Bitmap?
    {
        return try {
            val encodeByte: ByteArray = Base64.decode(image, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.size)
        } catch (e: Exception) {
            null
        }
    }

}

class MessageViewHolder
{
    lateinit var image : ImageView
    lateinit var avatar: ImageView
    lateinit var name: TextView
    lateinit var messageBody: TextView
    lateinit var date : TextView
}
