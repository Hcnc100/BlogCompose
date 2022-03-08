package com.nullpointer.blogcompose.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nullpointer.blogcompose.core.states.Resource
import com.nullpointer.blogcompose.domain.notify.NotifyRepoImpl
import com.nullpointer.blogcompose.domain.preferences.PreferencesRepoImpl
import com.nullpointer.blogcompose.models.Notify
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import timber.log.Timber
import java.lang.Exception
import java.util.concurrent.CancellationException
import javax.inject.Inject

@HiltViewModel
class NotifyViewModel @Inject constructor(
    private val notifyRepoImpl: NotifyRepoImpl,
    private val prefRepoImpl: PreferencesRepoImpl,
) : ViewModel() {
    val listNotify = flow<Resource<List<Notify>>> {
        prefRepoImpl.hasNewNotify.collect { hasNewNotify ->
            emit(Resource.Success(notifyRepoImpl.getNotify(!hasNewNotify)))
            if (hasNewNotify) prefRepoImpl.changeHasNotify(false)
        }
    }.catch { e ->
        emit(Resource.Failure(Exception(e)))
    }.stateIn(viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        Resource.Loading()
    )
}