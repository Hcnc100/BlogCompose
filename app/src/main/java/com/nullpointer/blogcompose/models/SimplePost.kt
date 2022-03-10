package com.nullpointer.blogcompose.models

import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import java.util.*

interface SimplePost {
    var description: String
    var urlImage: String
    var numberComments: Int
    var numberLikes: Int
    var ownerLike: Boolean
    var timeStamp: Date?
    var id: String
    var poster: Poster?
}