package com.syhler.android.messagingapp.utillities

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ListView
import android.widget.ProgressBar
import com.syhler.android.messagingapp.R

object ListViewHelper
{
    fun isTopReached(listView: ListView) : Boolean
    {
        val lView: View = listView.getChildAt(0)
        val offset = lView.top

        return offset == 0
    }

    fun getLoadingHeader(context: Context) : View
    {
        return (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.listview_header, null, false)
    }

    fun getLoadingHeaderProgressBar(header : View) : ProgressBar
    {
        return header.findViewById(R.id.chat_room_header_progress_loader)
    }
}