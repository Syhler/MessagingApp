package com.syhler.android.messagingapp.ui.chatroomlist.chatroom

import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.widget.ImageButton
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.syhler.android.messagingapp.R
import com.syhler.android.messagingapp.authenticate.CurrentUser
import com.syhler.android.messagingapp.data.entites.Message
import com.syhler.android.messagingapp.data.entites.User
import com.syhler.android.messagingapp.ui.chatroomlist.chatroom.adapter.MessageAdapter
import com.syhler.android.messagingapp.utillities.Dependencies
import com.syhler.android.messagingapp.viewmodels.ChatRoomViewModel
import kotlinx.android.synthetic.main.activity_chat_room.*
import java.io.ByteArrayOutputStream


class ChatRoomActivity : AppCompatActivity() {


    private val GALLERY_REQUEST_CODE: Int = 15
    private lateinit var viewModel : ChatRoomViewModel
    private lateinit var messageAdapter: MessageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_room)

        val chatRoomKey = intent.getStringExtra("key")
        if (chatRoomKey != null)
        {
            val factory = Dependencies.provideChatRoomViewModelFactory(chatRoomKey)
            viewModel = ViewModelProviders.of(this, factory)
                .get(ChatRoomViewModel::class.java)
        }

        messageAdapter =
            MessageAdapter(this)

        val listView = findViewById<ListView>(R.id.messages_view)
        listView.adapter = messageAdapter

        viewModel.getInitMessages().observe(this, Observer {
            if (it != null)
            {
                messageAdapter.addAll(it)
            }
        })




        viewModel.latestMessage.observe(this, Observer {
            if (it != null)
            {
                if (it.user.userAuthID != CurrentUser.getInstace().authenticationID)
                {
                    messageAdapter.add(it)
                }
            }
        })




        message_input_field.hint = chatRoomKey

        findViewById<ImageButton>(R.id.message_send_button).setOnClickListener { onMessageSend() }
        findViewById<ImageButton>(R.id.message_attach_button).setOnClickListener { onAttachUse() }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        super.onActivityResult(requestCode, resultCode, data)
        // Result code is RESULT_OK only if the user selects an Image
        when (requestCode) {
            GALLERY_REQUEST_CODE -> {
                //data.getData returns the content URI for the selected Image
                val selectedImage: Uri? = data?.data
                if (selectedImage != null)
                {
                    val filePathColumn =
                        arrayOf(MediaStore.Images.Media.DATA)
                    // Get the cursor
                    val cursor: Cursor? = contentResolver.query(selectedImage, filePathColumn, null, null, null)
                    // Move to first row
                    cursor?.moveToFirst()
                    //Get the column index of MediaStore.Images.Media.DATA
                    val columnIndex: Int = cursor?.getColumnIndex(filePathColumn[0]) ?: 0
                    //Gets the String value in the column
                    val imgDecodableString: String = cursor?.getString(columnIndex)!!
                    cursor?.close()

                    var bitmap = BitmapFactory.decodeFile(imgDecodableString)
                    bitmap = getResizedBitmap(bitmap, 420)

                    sendMessage("", imageToString(bitmap))
                }
            }
        }

    }

    private fun onAttachUse() {
        //Create an Intent with action as ACTION_PICK
        //Create an Intent with action as ACTION_PICK
        val intent = Intent(Intent.ACTION_PICK)
        // Sets the type as image/*. This ensures only components of type image are selected
        // Sets the type as image/*. This ensures only components of type image are selected
        intent.type = "image/*"
        //We pass an extra array with the accepted mime types. This will ensure only components with these MIME types as targeted.
        //We pass an extra array with the accepted mime types. This will ensure only components with these MIME types as targeted.
        val mimeTypes = arrayOf("image/jpeg", "image/png")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        // Launching the Intent
        // Launching the Intent
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }


    fun getResizedBitmap(image: Bitmap, maxSize: Int): Bitmap? {
        var width = image.width
        var height = image.height
        val bitmapRatio = width.toFloat() / height.toFloat()
        if (bitmapRatio > 0) {
            width = maxSize
            height = (width / bitmapRatio).toInt()
        } else {
            height = maxSize
            width = (height * bitmapRatio).toInt()
        }
        return Bitmap.createScaledBitmap(image, width, height, true)
    }

    fun onMessageSend()
    {
        val inputField = findViewById<TextView>(R.id.message_input_field)

        //make empty check
        sendMessage(inputField.text.toString(), "")

        inputField.setText("")

    }

    private fun sendMessage(text: String, image : String)
    {
        val currentUser = CurrentUser.getInstace()
        val user = User(currentUser.imageToString(), currentUser.fullName!!, currentUser.authenticationID)
        val message = Message(text, user, System.currentTimeMillis(), image)
        viewModel.addMessage(message)
        messageAdapter.add(message)
    }

    private fun imageToString(image : Bitmap) : String
    {
        val byteOutStream = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.PNG, 100, byteOutStream)
        val b: ByteArray = byteOutStream.toByteArray()
        return Base64.encodeToString(b, Base64.DEFAULT)
    }

}
