package com.nullpointer.blogcompose.domain.auth

import android.net.Uri
import com.nullpointer.blogcompose.models.CurrentUser
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val isDataComplete:Boolean
    val urlImgProfile: String?
    val nameUser: String?
    val uuidUser:String?

    suspend fun authWithTokeGoogle(token:String): Flow<Pair<String?, String?>>
    suspend fun deleterUser()
    suspend fun updatePhotoUser(urlImg:String):String
    suspend fun uploadNameUser(name:String):String
    suspend fun uploadDataUser(urlImg: String,name: String)
     fun getCurrentUser():Flow<CurrentUser?>
     fun logOut()
}