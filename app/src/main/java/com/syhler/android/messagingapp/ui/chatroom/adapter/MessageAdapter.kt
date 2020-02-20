package com.syhler.android.messagingapp.ui.chatroom.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.opengl.Visibility
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.storage.FirebaseStorage
import com.syhler.android.messagingapp.R
import com.syhler.android.messagingapp.authenticate.CurrentUser
import com.syhler.android.messagingapp.data.entites.Message
import com.syhler.android.messagingapp.utillities.BitmapManipulation
import java.text.SimpleDateFormat
import java.util.*


class MessageAdapter(private val context: Context) : BaseAdapter()
{

    private val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    private var holder = MessageViewHolder()

    private var messages : MutableList<Message> = arrayListOf()

    fun updateMessages(message : List<Message>) : Int
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


    @SuppressLint("SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View
    {
        val message = messages[position]
        val view : View

        if (message.isBelongToCurrentUser(CurrentUser.getInstance()))
        {
            view = setupViewHolderCurrentUser()

            view.tag = holder

            if (!message.imageUri.isBlank())
            {
                displayImage(message.imageUri)
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

            if (!message.imageUri.isBlank()) {
                displayImage(message.imageUri)
            }
            else {
                holder.messageBody.text = message.text
            }
        }
        return view
    }


    private fun displayImage(imageUri: String)
    {
        try {
            val ref = FirebaseStorage.getInstance().getReferenceFromUrl(imageUri)
            Glide.with(context)
                .load(ref)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_foreground)
                .into(holder.image)
        }catch (e : IllegalArgumentException) {
            Glide.with(context)
                .load(imageUri)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_foreground)
                .into(holder.image)
        }
        holder.messageBody.visibility = View.GONE

    }

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


