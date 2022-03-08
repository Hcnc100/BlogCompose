package com.nullpointer.blogcompose.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nullpointer.blogcompose.core.states.Resource
import com.nullpointer.blogcompose.domain.notify.NotifyRepoImpl
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
) : ViewModel() {
    val listNotify = flow<Resource<List<Notify>>> {
        notifyRepoImpl.getNotify().collect {
            emit(Resource.Success(it))
        }
    }.flowOn(Dispatchers.IO).catch { e ->
        emit(Resource.Failure(Exception(e)))
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        Resource.Loading()
    )
}