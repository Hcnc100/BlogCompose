package com.nullpointer.blogcompose.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.prefs.Preferences

class PreferencesDataSource(
    private val context: Context,
) {
    val Context.dataStore by preferencesDataStore(name = "settings")

    private val KEY_DATA_CHANGE = booleanPreferencesKey("key_data_change")

    val isDataExternChange: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[KEY_DATA_CHANGE] ?: true
    }

    suspend fun changeIsDataExternChange(change: Boolean) {
        context.dataStore.edit { settings ->
            settings[KEY_DATA_CHANGE] = change
        }
    }
}