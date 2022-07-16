package com.nullpointer.blogcompose.models.users

import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class MyUser(
    @set:Exclude @get:Exclude
    override var idUser: String = "",
    override var name: String = "",
    override var urlImg: String = "",
    var emailUser:String="",
    var token: String = "",
    @ServerTimestamp
    var timeCreate: Date? = null,
    @ServerTimestamp
    var timeUpdate: Date? = null,
) : SimpleUser(), Parcelable {

    val isUserAuth get() = idUser.isNotEmpty()
    val isDataComplete get() = name.isNotEmpty() && urlImg.isNotEmpty()

    fun toInnerUser(): InnerUser {
        return InnerUser(
            idUser = idUser,
            name = name,
            urlImg = urlImg
        )
    }

}