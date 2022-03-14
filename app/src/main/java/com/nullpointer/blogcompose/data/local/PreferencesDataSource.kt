package com.nullpointer.blogcompose.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.nullpointer.blogcompose.models.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.prefs.Preferences

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
                .setUuid(user.uuid)
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
                uuid = user.uuid,
                urlImg = user.urlImg
            )
        }

}