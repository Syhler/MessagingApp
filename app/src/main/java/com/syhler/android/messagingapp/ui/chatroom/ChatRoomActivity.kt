package com.syhler.android.messagingapp.ui.chatroom

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.syhler.android.messagingapp.R
import com.syhler.android.messagingapp.authenticate.CurrentUser
import com.syhler.android.messagingapp.data.entites.Message
import com.syhler.android.messagingapp.data.entites.MessageUser
import com.syhler.android.messagingapp.notification.SendingNotification
import com.syhler.android.messagingapp.ui.chatroom.adapter.MessageRecycleViewAdapter
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

    private val CAMERA_REQUEST_CODE: Int = 3
    private val CAMERA_PERMISSION_CODE: Int = 2
    private val GALLERY_REQUEST_CODE: Int = 1

    private var chatRoomKey: String? = ""
    private var chatRoomName : String? = ""

    private lateinit var viewModel : ChatRoomViewModel
    private lateinit var inputField : EditText
    private lateinit var activityBar: LinearLayout


    private lateinit var recyclerView : RecyclerView
    private lateinit var messageAdapter: MessageRecycleViewAdapter


    private lateinit var pullToRefresh : SwipeRefreshLayout

    private var notification = SendingNotification(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_room)


        chatRoomKey = intent.getStringExtra(KeyFields.chatRoomKey)

        chatRoomName = intent.getStringExtra(KeyFields.chatRoomName)

        title = chatRoomName

        if (chatRoomKey != null) {
            viewModel = createViewModel(chatRoomKey!!)
            CurrentUser.getInstance().chatRoomKey = chatRoomKey!!
        }
        inputField = findViewById(R.id.message_input_field)
        activityBar = findViewById(R.id.chat_room_activity_bar)
        setupRecyclerView()

        initListViewMessages()

        observeIncomingMessages()

        setKeyboardListener()

        pullDownToRefresh()

        //Sets button actions
        findViewById<ImageButton>(R.id.message_send_button).setOnClickListener { onMessageClick() }
        findViewById<ImageButton>(R.id.message_attach_button).setOnClickListener { onAttachClick() }
        findViewById<ImageButton>(R.id.message_camera_button).setOnClickListener { onCameraClick() }
        findViewById<ImageButton>(R.id.message_add_button).setOnClickListener {onAddClick()}

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK) {
            onImagePick(data)
        } else if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            onCameraUse(data)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == CAMERA_PERMISSION_CODE)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show()
                openCamera()
            }
            else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show()
            }
        }
    }


    private fun onAddClick()
    {
        if (activityBar.visibility == View.GONE) {
            activityBar.visibility = View.VISIBLE
        }
        else {
            hideActivityBar()
        }
    }

    private fun setupRecyclerView()
    {
        recyclerView = findViewById(R.id.messages_view)

        recyclerView.apply {
            messageAdapter = MessageRecycleViewAdapter()
            adapter = messageAdapter
        }

        //When user open keyboard scroll down to the latest message
        recyclerView.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            if (bottom < oldBottom) {
                recyclerView.postDelayed({ recyclerView.smoothScrollToPosition(messageAdapter.itemCount-1) }, 100)
            }
        }
    }

    private fun pullDownToRefresh()
    {
        pullToRefresh = findViewById(R.id.pullToRefresh)
        pullToRefresh.setOnRefreshListener {
            if (!viewModel.loadedAllMessages)
            {
                val task = viewModel.loadPreviousMessages(messageAdapter.messages)

                if (task == null) {
                    pullToRefresh.isRefreshing = false
                }

            } else {
                pullToRefresh.isRefreshing = false
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
        val factory = Dependencies.provideChatRoomViewModelFactory(chatRoomKey, contentResolver)
        return ViewModelProviders.of(this, factory)
            .get(ChatRoomViewModel::class.java)
    }

    private fun initListViewMessages()
    {
        viewModel.getInitMessages().observe(this, Observer { messages ->
            if (messages != null)
            {
                val messagesBeforeUpdate = messageAdapter.submitMessages(messages)


                if (recyclerView.childCount > 0 && pullToRefresh.isRefreshing) {
                    loadPreviousMessages((messages.size-messagesBeforeUpdate))
                }
                else{
                    scrollDownToLatestMessage()
                }

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
                    val notificationSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                    val sound = RingtoneManager.getRingtone(this, notificationSoundUri)
                    sound.play()
                    messageAdapter.add(message)

                    if (!recyclerView.canScrollVertically(1))
                    {
                       scrollDownToLatestMessage()
                    }
                }
            }
        })
    }


    private fun loadPreviousMessages(index: Int)
    {
        pullToRefresh.isRefreshing = false

        val layoutManager = recyclerView.layoutManager as LinearLayoutManager

        val currentIndexInUpdatedListView: Int = layoutManager.findFirstVisibleItemPosition() + index

        layoutManager.scrollToPositionWithOffset(currentIndexInUpdatedListView,0)
    }

    private fun onMessageClick()
    {
        if (!inputField.text.isBlank())
        {
            sendMessage(Uri.EMPTY)
        }
    }

    private fun onImagePick(data : Intent?)
    {
        val selectedImage: Uri? = data?.data
        if (selectedImage != null)
        {
            sendMessage(selectedImage)
        }
    }

    /**
     * Opens gallery
     */
    private fun onAttachClick() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)

        intent.type = "image/*" //Acceptable files

        val mimeTypes = arrayOf("image/jpeg", "image/png") //Acceptable files
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        hideActivityBar()
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }


    private fun openCamera()
    {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE)
        hideActivityBar()
    }

    private fun onCameraUse(data: Intent?)
    {
        if (data == null) return

        val photo = data.extras?.get("data") as Bitmap
        val uri = BitmapManipulation.toUriConverter(photo, this)
        if (uri != null) {
            sendMessage(uri)
        }
    }

    private fun onCameraClick()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE)
            } else {
                openCamera()
            }
        }
        else {
            openCamera()
        }

    }


    private fun setKeyboardListener()
    {
        inputField.setOnEditorActionListener { textView, actionId, keyEvent ->

            if (actionId == EditorInfo.IME_ACTION_DONE ||
                keyEvent.action == KeyEvent.ACTION_DOWN ||
                keyEvent.action == KeyEvent.KEYCODE_ENTER)
            {
                onMessageClick()
                return@setOnEditorActionListener true
            }

            return@setOnEditorActionListener false
        }
    }


    private fun sendMessage(image : Uri)
    {
        if (!TempNotificationClass.hasOpenedDialog) openNotificationDialog()

        val currentUser = CurrentUser.getInstance()
        val user = MessageUser(currentUser.getImageAsByte(), currentUser.fullName!!, currentUser.authenticationID) // should change to get image uri
        val message = Message(inputField.text.toString(), user, System.currentTimeMillis(), image.toString())
        inputField.setText("")
        viewModel.addMessage(message)
        messageAdapter.add(message)
        notification.sendNotification(chatRoomKey, message, chatRoomName)
        scrollDownToLatestMessage()
        hideActivityBar()

    }

    private fun hideActivityBar() {
        activityBar.visibility = View.GONE
    }

    private fun scrollDownToLatestMessage()
    {
        recyclerView.postDelayed({recyclerView.scrollToPosition(messageAdapter.itemCount-1)},150)
    }


    override fun onBackPressed() {
        super.onBackPressed()
        CurrentUser.getInstance().chatRoomKey = ""
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
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
