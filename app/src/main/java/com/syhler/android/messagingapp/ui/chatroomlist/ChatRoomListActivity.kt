package com.syhler.android.messagingapp.ui.chatroomlist

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.syhler.android.messagingapp.R
import com.syhler.android.messagingapp.authenticate.AuthenticationHandler
import com.syhler.android.messagingapp.authenticate.CurrentUser
import com.syhler.android.messagingapp.authenticate.enums.AuthenticationMethod
import com.syhler.android.messagingapp.data.entites.ChatRoom
import com.syhler.android.messagingapp.ui.LoginActivity
import com.syhler.android.messagingapp.ui.chatroom.ChatRoomActivity
import com.syhler.android.messagingapp.ui.chatroomlist.adapter.ChatRoomListRecyclerAdapter
import com.syhler.android.messagingapp.utillities.Dependencies
import com.syhler.android.messagingapp.utillities.KeyFields
import com.syhler.android.messagingapp.viewmodels.ChatRoomListViewModel


class ChatRoomListActivity : AppCompatActivity(), View.OnClickListener{

    private lateinit var  authenticationHandler : AuthenticationHandler

    private var viewModel : ChatRoomListViewModel? = null

    private lateinit var chatRoomAdapter : ChatRoomListRecyclerAdapter
    private lateinit var recyclerView : RecyclerView
    private lateinit var progressBar: ProgressBar



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_room_list)

        title = "Chat Rooms"

        viewModel = createViewModel()

        progressBar = findViewById(R.id.progress_bar)

        authenticationHandler = AuthenticationHandler(this, getString(R.string.default_web_client_id), null)

        pullDownToRefresh()

        setupRecyclerView()

        findViewById<Button>(R.id.button_log_out).setOnClickListener(this)

        initListViewChatRooms()
    }

    private fun setupRecyclerView()
    {

        recyclerView = findViewById(R.id.recycler_chat_rooms)


        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@ChatRoomListActivity)
            chatRoomAdapter = ChatRoomListRecyclerAdapter(recyclerViewOnClick())
            adapter = chatRoomAdapter

        }
    }

    private fun recyclerViewOnClick() : View.OnClickListener
    {
        return View.OnClickListener { view ->
            if (view != null) {
                val itemPosition = recyclerView.getChildLayoutPosition(view)
                changeActivityToChatRoom(chatRoomAdapter.chatRooms, itemPosition)
            }
        }
    }


    private fun createViewModel() : ChatRoomListViewModel
    {
        val factory = Dependencies.provideChatRoomListViewModelFactory()
        return ViewModelProviders.of(this, factory)
            .get(ChatRoomListViewModel::class.java)
    }

    private fun initListViewChatRooms()
    {
        viewModel?.getChatRooms()?.observe(this, Observer { chatRooms ->

            chatRoomAdapter.submitList(chatRooms)
            progressBar.visibility = View.GONE
        })
    }

    private fun pullDownToRefresh()
    {
        val pullToRefreshLayout = findViewById<SwipeRefreshLayout>(R.id.pullToRefresh)
        pullToRefreshLayout.setOnRefreshListener {
            if (viewModel != null) {
                viewModel!!.updateChatRooms().addOnSuccessListener {
                    pullToRefreshLayout.isRefreshing = false
                }
            }
        }
    }

    private fun changeActivityToChatRoom(chatRooms : List<ChatRoom>, position : Int)
    {
        val intent = Intent(this, ChatRoomActivity::class.java)
        if (chatRooms.size > position)
        {
            val chatRoom = chatRooms[position]
            intent.putExtra(KeyFields.chatRoomKey, chatRoom.key) //TODO(create a class that hold all intent values)
            intent.putExtra(KeyFields.chatRoomName, chatRoom.title)
        }
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    private fun signOut() {
        when(CurrentUser.getInstance().authenticationMethod)
        {
            AuthenticationMethod.FACEBOOK ->
            {
                authenticationHandler.facebook.signOut()
                CurrentUser.initialize() // empties the current user data
                changeToLoginScreen()

            }
            AuthenticationMethod.GOOGLE -> {
                authenticationHandler.google.signOut()?.addOnCompleteListener {
                    CurrentUser.initialize() // empties the current user data
                    changeToLoginScreen()
                }
            }
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.button_log_out -> signOut()
        }
    }

    override fun onBackPressed() {
        //Nothing shall happen - shouldn't be allowed to go back to login activity without the use logging off
    }

    private fun changeToLoginScreen()
    {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        if (viewModel != null)
        {
            viewModel!!.updateChatRooms()
        }
    }
}
