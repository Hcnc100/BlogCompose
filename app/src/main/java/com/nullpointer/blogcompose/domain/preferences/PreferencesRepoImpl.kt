package com.nullpointer.blogcompose.domain.preferences

import com.nullpointer.blogcompose.data.local.PreferencesDataSource
import kotlinx.coroutines.flow.Flow

class PreferencesRepoImpl(
    private val preferencesDataSource: PreferencesDataSource
) : PreferencesRepository {
    override val isDataChange: Flow<Boolean> =
        preferencesDataSource.isDataExternChange

    override suspend fun changeData(isDataChange: Boolean)=
        preferencesDataSource.changeIsDataExternChange(isDataChange)

}