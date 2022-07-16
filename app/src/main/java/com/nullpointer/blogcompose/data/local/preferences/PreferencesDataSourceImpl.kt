package com.nullpointer.blogcompose.data.local.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.nullpointer.blogcompose.data.local.UserPreference
import com.nullpointer.blogcompose.data.local.UserSerializer
import com.nullpointer.blogcompose.models.users.MyUser
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class PreferencesDataSourceImpl(
    private val context: Context
) : PreferencesDataSource {

    private val Context.userProtoDataStore: DataStore<UserPreference> by dataStore(
        fileName = "myUser.pb",
        serializer = UserSerializer
    )

    override val user = context.userProtoDataStore.data.map { user ->
        MyUser(
            idUser = user.uuid,
            name = user.name,
            urlImg = user.urlImg,
        )
    }

    override suspend fun getIdUser(): String {
        return user.first().idUser
    }

    override suspend fun getCurrentUser(): MyUser {
        return user.first()
    }


    override suspend fun updateUser(myUser: MyUser) {
        context.userProtoDataStore.updateData { currentUserData ->
            currentUserData.toBuilder()
                .setName(myUser.name)
                .setUuid(myUser.idUser)
                .setUrlImg(myUser.urlImg)
                .build()
        }
    }

    override suspend fun deleterUser() {
        context.userProtoDataStore.updateData { currentUserData ->
            currentUserData.toBuilder()
                .setName("")
                .setUuid("")
                .setUrlImg("")
                .build()
        }
    }

}