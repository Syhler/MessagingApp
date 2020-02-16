package com.syhler.android.messagingapp.utillities

import java.sql.Timestamp
import java.util.*

object DateManipulation
{

    fun convertTimespanToDate(timestamp: Long) : Date
    {
        val ts = Timestamp(timestamp)
        return Date(ts.time)
    }

}