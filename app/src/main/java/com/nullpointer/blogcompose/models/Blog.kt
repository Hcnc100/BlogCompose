package com.nullpointer.blogcompose.models

import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import java.util.*


@Parcelize
data class Post(
    val description:String="",
    val urlImage:String="",
    val numberComments:Int=0,
    var numberLikes:Int=0,
    val poster: Poster?=null,
    @set:Exclude @get:Exclude
    var ownerLike:Boolean=false,
    @ServerTimestamp
    var timeStamp: Date?=null,
    @set:Exclude @get:Exclude
    var id:String=UUID.randomUUID().toString(),
) : Parcelable

@Parcelize
data class Poster(
    val uuid:String="",
    val name:String="",
    val urlImg:String=""
) : Parcelable
