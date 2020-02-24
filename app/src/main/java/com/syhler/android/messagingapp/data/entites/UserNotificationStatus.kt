package com.syhler.android.messagingapp.data.entites

class UserNotificationStatus(val isSubscriptedToChatRoom : Boolean, val chatRoomKey : String)
{
    constructor() : this(false, "")
}