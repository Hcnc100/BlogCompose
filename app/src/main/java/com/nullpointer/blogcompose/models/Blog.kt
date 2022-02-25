package com.nullpointer.blogcompose.models

import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class Post(
    val description:String="",
    val profilePictureOwner:String="",
    val postOwnerName:String="",
    val postOwnerId:String="",
    val urlImage:String="",
    val numberLikes:Long=0,
    val numberComments:Long=0,
    @ServerTimestamp
    var timeStamp: Date?=null,
    @set:Exclude @get:Exclude
    var id:String=UUID.randomUUID().toString(),
) : Parcelable {
    companion object{
        fun createRandom():Post{
            return Post(
                description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec quis euismod tortor. Aliquam finibus, ex ut viverra facilisis, elit leo tempor ante, in rutrum ipsum sapien id sem. Cras sit amet mattis lacus. Suspendisse commodo feugiat aliquam. Nunc at orci eget felis commodo scelerisque. Phasellus placerat viverra eros id sodales. In hac habitasse platea dictumst.",
                profilePictureOwner = "https://picsum.photos/500",
                postOwnerName = "Pancito",
                postOwnerId = "1",
                urlImage = "https://picsum.photos/500",
                numberComments = (0L..100L).random(),
                numberLikes = (0L..100L).random(),
            )
        }
    }
}