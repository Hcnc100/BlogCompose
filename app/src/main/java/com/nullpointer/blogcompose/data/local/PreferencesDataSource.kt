package com.nullpointer.blogcompose.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.nullpointer.blogcompose.models.users.MyUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PreferencesDataSource(
    private val context: Context,
) {
    private val Context.userProtoDataStore: DataStore<UserPreference> by dataStore(
        fileName = "myUser.pb",
        serializer = UserSerializer
    )

    suspend fun saveUser(myUser: MyUser) {
        context.userProtoDataStore.updateData { currentUserData ->
            currentUserData.toBuilder()
                .setName(myUser.name)
                .setUuid(myUser.idUser)
                .setUrlImg(myUser.urlImg)
                .build()
        }
    }

    suspend fun deleterUser() {
        context.userProtoDataStore.updateData { currentUserData ->
            currentUserData.toBuilder()
                .setName("")
                .setUuid("")
                .setUrlImg("")
                .build()
        }
    }


    fun getUserFromProtoStore(): Flow<MyUser> =
        context.userProtoDataStore.data.map { user ->
            MyUser(
                idUser = user.uuid,
                name = user.name,
                urlImg = user.urlImg,
            )
        }

}