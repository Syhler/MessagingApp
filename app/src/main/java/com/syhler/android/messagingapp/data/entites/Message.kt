package com.syhler.android.messagingapp.data.entites

import com.google.firebase.database.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import com.syhler.android.messagingapp.authenticate.CurrentUser
import java.util.*


@IgnoreExtraProperties
data class Message(
    var text: String,
    val user: User,
    val timespan: Long)
{

    @set:Exclude @get:Exclude var date : Date = Date()

    constructor() : this("", User(),0L)

    fun isBelongToCurrentUser(currentUser: CurrentUser) : Boolean
    {
        return currentUser.authenticationID == user.userAuthID
    }


}