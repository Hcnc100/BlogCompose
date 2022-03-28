package com.nullpointer.blogcompose.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.nullpointer.blogcompose.models.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PreferencesDataSource(
    private val context: Context,
) {
    private val Context.userProtoDataStore: DataStore<UserPreference> by dataStore(
        fileName = "user.pb",
        serializer = UserSerializer
    )

    suspend fun saveUser(user: User) {
        context.userProtoDataStore.updateData { currentUserData ->
            currentUserData.toBuilder()
                .setName(user.nameUser)
                .setUuid(user.idUser)
                .setUrlImg(user.urlImg)
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


    fun getUserFromProtoStore(): Flow<User> =
        context.userProtoDataStore.data.map { user ->
            User(
                nameUser = user.name,
                idUser = user.uuid,
                urlImg = user.urlImg
            )
        }

}