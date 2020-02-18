package com.syhler.android.messagingapp.ui.chatroom

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ListView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.syhler.android.messagingapp.R
import com.syhler.android.messagingapp.authenticate.CurrentUser
import com.syhler.android.messagingapp.data.entites.Message
import com.syhler.android.messagingapp.data.entites.MessageUser
import com.syhler.android.messagingapp.notification.SendingNotification
import com.syhler.android.messagingapp.ui.chatroom.adapter.MessageAdapter
import com.syhler.android.messagingapp.ui.dialogs.AskForNotificationDialog
import com.syhler.android.messagingapp.utillities.Dependencies
import com.syhler.android.messagingapp.utillities.KeyFields
import com.syhler.android.messagingapp.utillities.ListViewHelper
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
    private lateinit var inputField : EditText

    private lateinit var animationListViewHeader : ProgressBar
    private lateinit var listView : ListView
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

        setupListView()

        initListViewMessages()

        observeIncomingMessages()

        setKeyboardListener()

        pullDownToRefresh()

        //Sets button actions
        findViewById<ImageButton>(R.id.message_send_button).setOnClickListener { sendMessage(Uri.EMPTY) }
        findViewById<ImageButton>(R.id.message_attach_button).setOnClickListener { onAttachUse() }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GALLERY_REQUEST_CODE &&
            resultCode == RESULT_OK &&
            data != null && data.data != null)
        {
            onImagePick(data)
        }

    }

    private fun setupListView()
    {
        messageAdapter = MessageAdapter(this)
        listView = findViewById(R.id.messages_view)
        listView.adapter = messageAdapter

        val listViewHeader = ListViewHelper.getLoadingHeader(this)
        animationListViewHeader = ListViewHelper.getLoadingHeaderProgressBar(listViewHeader)
        listView.addHeaderView(listViewHeader)
    }

    private fun pullDownToRefresh()
    {
        pullToRefresh = findViewById(R.id.pullToRefresh)
        pullToRefresh.setOnRefreshListener {
            if (!viewModel.loadedAllMessages)
            {
                val task = viewModel.loadPreviousMessages()

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
                val countBeforeUpdated = messageAdapter.updateMessages(messages)

                if (listView.childCount > 0 && pullToRefresh.isRefreshing)
                {
                    pullToRefresh.isRefreshing = false
                    val currentIndexInUpdatedListView: Int = listView.firstVisiblePosition + (messages.size-countBeforeUpdated)
                    val lView: View = listView.getChildAt(0)
                    val top = lView.top
                    listView.setSelectionFromTop(currentIndexInUpdatedListView, top)
                }


                animationListViewHeader.visibility = View.GONE
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
            sendMessage(selectedImage)
        }
    }

    //open gallery
    private fun onAttachUse() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)

        intent.type = "image/*" //Acceptable files

        val mimeTypes = arrayOf("image/jpeg", "image/png") //Acceptable files
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)

        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    private fun setKeyboardListener()
    {
        inputField.setOnEditorActionListener { textView, actionId, keyEvent ->

            if (actionId == EditorInfo.IME_ACTION_DONE ||
                keyEvent.action == KeyEvent.ACTION_DOWN ||
                keyEvent.action == KeyEvent.KEYCODE_ENTER)
            {
                sendMessage(Uri.EMPTY)
                return@setOnEditorActionListener true
            }

            return@setOnEditorActionListener false
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        CurrentUser.getInstance().chatRoomKey = ""
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
        scrollToBottomOfListView()
    }

    private fun scrollToBottomOfListView()
    {
        listView.post {
            listView.setSelection(messageAdapter.getCount() - 1)
        }
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
