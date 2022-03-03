package com.nullpointer.blogcompose.domain.preferences

import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {
    val isDataChange:Flow<Boolean>
}