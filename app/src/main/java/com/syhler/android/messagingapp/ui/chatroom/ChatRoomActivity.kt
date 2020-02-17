package com.syhler.android.messagingapp.ui.chatroom

import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageButton
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.syhler.android.messagingapp.R
import com.syhler.android.messagingapp.authenticate.CurrentUser
import com.syhler.android.messagingapp.data.entites.Message
import com.syhler.android.messagingapp.data.entites.MessageUser
import com.syhler.android.messagingapp.notification.SendingNotification
import com.syhler.android.messagingapp.ui.chatroom.adapter.MessageAdapter
import com.syhler.android.messagingapp.ui.dialogs.AskForNotificationDialog
import com.syhler.android.messagingapp.utillities.BitmapManipulation
import com.syhler.android.messagingapp.utillities.Dependencies
import com.syhler.android.messagingapp.utillities.KeyFields
import com.syhler.android.messagingapp.viewmodels.ChatRoomViewModel


class TempNotificationClass
{
    companion object
    {
        var hasOpenedDialog = false //TODO(REMOVE WHEN IMPLEMENTED SQLITE)
    }
}

class ChatRoomActivity : AppCompatActivity() {


    //TODO(null check viewmodel)

    private val GALLERY_REQUEST_CODE: Int = 1
    private lateinit var viewModel : ChatRoomViewModel
    private lateinit var messageAdapter: MessageAdapter
    private var chatRoomKey: String? = ""
    private var chatRoomName : String? = ""


    private var notification = SendingNotification(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_room)

        chatRoomKey = intent.getStringExtra(KeyFields.chatRoomKey)

        chatRoomName = intent.getStringExtra(KeyFields.chatRoomName)


        if (chatRoomKey != null) {
            viewModel = createViewModel(chatRoomKey!!)
            //FirebaseMessaging.getInstance().unsubscribeFromTopic(chatRoomKey!!)
            CurrentUser.getInstance().chatRoomKey = chatRoomKey!!
        }

        messageAdapter = MessageAdapter(this)

        val listView = findViewById<ListView>(R.id.messages_view)
        listView.adapter = messageAdapter

        initListViewMessages()

        observeIncomingMessages()

        //Sets button actions
        findViewById<ImageButton>(R.id.message_send_button).setOnClickListener { onMessageSend() }
        findViewById<ImageButton>(R.id.message_attach_button).setOnClickListener { onAttachUse() }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        super.onActivityResult(requestCode, resultCode, data)
        // Result code is RESULT_OK only if the user selects an Image
        when (requestCode) {
            GALLERY_REQUEST_CODE -> {
                onImagePick(data)
            }
        }
    }

    private fun openNotificationDialog()
    {
        if (chatRoomName != null && chatRoomKey != null)
        {
            val notificationDialog = AskForNotificationDialog(chatRoomKey!!)
            notificationDialog.show(supportFragmentManager, "NotificationDialog")
            TempNotificationClass.hasOpenedDialog = true

        }
    }

    private fun createViewModel(chatRoomKey : String) : ChatRoomViewModel
    {
        val factory = Dependencies.provideChatRoomViewModelFactory(chatRoomKey)
        return ViewModelProviders.of(this, factory)
            .get(ChatRoomViewModel::class.java)
    }

    private fun initListViewMessages()
    {
        viewModel.getInitMessages().observe(this, Observer { messages ->
            if (messages != null)
            {
                messageAdapter.addAll(messages)
            }
        })
    }

    private fun observeIncomingMessages()
    {
        viewModel.latestMessage.observe(this, Observer {message ->
            if (message != null)
            {
                if (message.messageUser.userAuthID != CurrentUser.getInstance().authenticationID)
                {
                    messageAdapter.add(message)
                }
            }
        })
    }

    private fun onImagePick(data : Intent?)
    {
        val selectedImage: Uri? = data?.data
        if (selectedImage != null)
        {
            val path = getSelectedImagePath(selectedImage)

            var bitmap = BitmapManipulation.fromPath(path)
            bitmap = BitmapManipulation.getResized(bitmap, 420)!!

            sendMessage("", BitmapManipulation.toByte(bitmap))
        }
    }

    private fun getSelectedImagePath(selectedImage : Uri) : String
    {
        val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
        // Get the cursor
        val cursor: Cursor? = contentResolver.query(selectedImage, filePathColumn, null, null, null)
        // Move to first row
        cursor?.moveToFirst()
        //Get the column index of MediaStore.Images.Media.DATA
        val columnIndex: Int = cursor?.getColumnIndex(filePathColumn[0]) ?: 0
        //Gets the String value in the column
        val imagePath: String = cursor?.getString(columnIndex)!!
        cursor.close()
        return imagePath
    }

    //open gallery
    private fun onAttachUse() {
        val intent = Intent(Intent.ACTION_PICK)

        intent.type = "image/*" //Acceptable files

        val mimeTypes = arrayOf("image/jpeg", "image/png") //Acceptable files
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)

        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    private fun onMessageSend()
    {
        if (!TempNotificationClass.hasOpenedDialog) openNotificationDialog()

        val inputField = findViewById<TextView>(R.id.message_input_field)
        if (!inputField.text.toString().isBlank())
        {
            sendMessage(inputField.text.toString(), "")
        }
        inputField.text = ""
    }

    override fun onBackPressed() {
        super.onBackPressed()
        CurrentUser.getInstance().chatRoomKey = ""
    }

    private fun sendMessage(text: String, image : String)
    {
        val currentUser = CurrentUser.getInstance()
        val user = MessageUser(currentUser.getImageAsByte(), currentUser.fullName!!, currentUser.authenticationID)
        val message = Message(text, user, System.currentTimeMillis(), image)
        viewModel.addMessage(message)
        messageAdapter.add(message)
        notification.sendNotification(chatRoomKey, message, chatRoomName)
    }

    override fun onPause() {
        super.onPause()
        CurrentUser.getInstance().chatRoomKey = ""
    }

    override fun onResume() {
        super.onResume()
        CurrentUser.getInstance().chatRoomKey = chatRoomKey!!
    }
}
