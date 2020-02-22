package com.syhler.android.messagingapp.ui.chatroom.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.syhler.android.messagingapp.R
import com.syhler.android.messagingapp.authenticate.CurrentUser
import com.syhler.android.messagingapp.data.entites.Message
import com.syhler.android.messagingapp.utillities.BitmapManipulation
import java.text.SimpleDateFormat
import java.util.*

class MessageRecycleViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>()
{

    private var messages : MutableList<Message> = arrayListOf()

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
        notifyItemInserted(messages.size-1)
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
        //holder.setIsRecyclable(false);


        when(holder)
        {
            is CurrentUserMessageViewHolder ->
            {
                holder.bind(messages[ holder.adapterPosition], holder.adapterPosition)
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

    class IncomingMessageViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        private val image : ImageView = itemView.findViewById(R.id.message_image)
        private val avatar : ImageView = itemView.findViewById(R.id.message_avatar)
        private val name : TextView = itemView.findViewById(R.id.message_owner_name)
        private val messageBody : TextView = itemView.findViewById(R.id.message_body)

        @SuppressLint("SetTextI18n")
        fun bind(message: Message)
        {
            name.text = message.messageUser.fullName + " - " + getTimeNow(message.date)
            setIncomingPicture(message.messageUser.image)


            if (!message.imageUri.isBlank())
            {
                val ref = FirebaseStorage.getInstance().getReferenceFromUrl(message.imageUri)
                Glide.with(itemView.context)
                    .load(ref)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_foreground)
                    .override(500,500)
                    .dontAnimate()
                    .into(image)
                messageBody.visibility = View.GONE
            }
            else
            {
                messageBody.text = message.text
            }
        }

        //TODO(update so that the image get saved in firebase)
        private fun setIncomingPicture(imageBinary: String?)
        {
            val image = BitmapManipulation.getFromByte(imageBinary)
            if (image != null)
            {
                avatar.setImageBitmap(image)
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
    }

    class CurrentUserMessageViewHolder constructor(itemView : View) : RecyclerView.ViewHolder(itemView)
    {
        private val image : ImageView = itemView.findViewById(R.id.message_image)
        private val messageBody : TextView = itemView.findViewById(R.id.message_body)

        fun bind(message: Message, position: Int)
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

}