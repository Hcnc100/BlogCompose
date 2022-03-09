package com.nullpointer.blogcompose.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nullpointer.blogcompose.core.delegates.SavableProperty
import com.nullpointer.blogcompose.core.states.Resource
import com.nullpointer.blogcompose.core.utils.NetworkException
import com.nullpointer.blogcompose.domain.post.PostRepoImpl
import com.nullpointer.blogcompose.domain.preferences.PreferencesRepoImpl
import com.nullpointer.blogcompose.models.Post
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import timber.log.Timber
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(
    private val postRepo: PostRepoImpl,
    private val prefRepo: PreferencesRepoImpl,
) : ViewModel() {

//    private val _listPost = MutableStateFlow<Resource<List<Post>>>(Resource.Loading())
//    val listPost = _listPost.asStateFlow()
//
//    private val _listMyPost = MutableStateFlow<Resource<List<Post>>>(Resource.Loading())
//    val listMyPost = _listMyPost.asStateFlow()

    private val _messagePost = Channel<String>()
    val messagePost = _messagePost.receiveAsFlow()

    private var jobRequestNew: Job? = null
    private val _stateLoadData = MutableStateFlow<Resource<Unit>>(Resource.Loading())
    val stateLoad = _stateLoadData.asStateFlow()

    private var jobConcatenatePost: Job? = null
    private val _stateConcatenateData = MutableStateFlow<Resource<Unit>>(Resource.Loading())
    val stateConcatenate = _stateConcatenateData.asStateFlow()

    val listPost = flow<Resource<List<Post>>> {
        postRepo.getLastPost(false).collect {
            emit(Resource.Success(it))
        }
    }.catch {
        Resource.Failure<Resource<List<Post>>>(Exception(it))
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        Resource.Loading()
    )

    init {
        requestNewPost()
    }


    fun requestNewPost() {
        jobRequestNew?.cancel()
        jobRequestNew = viewModelScope.launch {
            _stateLoadData.value = Resource.Loading()
            try {
                val sizeNewPost = postRepo.requestNewPost()
                if (sizeNewPost == 0) {
                    _messagePost.trySend("Es todo, no hay post nuevos")
                } else {
                    _messagePost.trySend("Se obtuvieron $sizeNewPost post nuevos")
                }
                _stateLoadData.value = Resource.Success(Unit)
            } catch (e: Exception) {
                _stateLoadData.value = Resource.Failure(e)
                when (e) {
                    is CancellationException -> throw e
                    is NetworkException -> _messagePost.trySend("Verifique su conexion a internet")
                    else -> {
                        _messagePost.trySend("Error desconocido")
                        Timber.d("Error en el request $e")
                    }
                }
            }
        }
    }

    fun concatenatePost() {
        jobConcatenatePost?.cancel()
        jobConcatenatePost = viewModelScope.launch {
            _stateConcatenateData.value = Resource.Loading()
            try {
                val sizeRequest = postRepo.concatenatePost()
                Timber.d("Datos concatenados $sizeRequest")
                _stateConcatenateData.value = Resource.Success(Unit)
            } catch (e: Exception) {
                _stateConcatenateData.value = Resource.Failure(e)
                when (e) {
                    is CancellationException -> throw e
                    is NetworkException -> _messagePost.trySend("Verifique su conexion a internet")
                    else -> {
                        _messagePost.trySend("Error desconocido")
                        Timber.d("Error en el request $e")
                    }
                }
            }
        }
    }

    fun likePost(oldPost: Post, isLiked: Boolean) = viewModelScope.launch(Dispatchers.IO) {
        try {
            postRepo.updateLikePost(oldPost, isLiked)
        } catch (e: Exception) {
            when (e) {
                is CancellationException -> throw e
                is NetworkException -> _messagePost.send("Necesita conexion para esto")
                else -> {
                    Timber.d("Erro al dar like $e")
                    _messagePost.send("Error desconocido")
                }
            }
        }
    }

//    fun fetchLastPost() = viewModelScope.launch(Dispatchers.IO) {
//        _listPost.value = Resource.Loading()
//        _listPost.value = try {
//            Resource.Success(postRepo.getLastPost(false))
//        } catch (e: Exception) {
//            Timber.e(e)
//            Resource.Failure(e)
//        }
//    }

//    fun fetchMyLastPost() = viewModelScope.launch {
//        _listMyPost.value = Resource.Loading()
//        _listMyPost.value = try {
//            Resource.Success(postRepo.getMyLastPost("", false))
//        } catch (e: Exception) {
//            Resource.Failure(e)
//        }
//    }

//    fun concatenateLastPost() = viewModelScope.launch(Dispatchers.IO) {
//        try {
//            if (enableGetLastPost) {
//                val sizeEstimated = currentSizeLastPost + NEW_POST_REQUEST
//                val listNewPost = postRepo.getLastPost(sizeEstimated)
//                currentSizeLastPost = listNewPost.size
//                _listPost.value = Resource.Success(listNewPost)
//                if (listNewPost.size < sizeEstimated) {
//                    enableGetLastPost = false
//                    Timber.d("Limite alcalzado se desactivo la carga infinita de todos")
//                }
//            }
//        } catch (e: Exception) {
//            Timber.d("Excepcion con la carga concatedana ${e.message}")
//            if (e is CancellationException) throw e
//        }
//    }


//    fun likePost(oldPost: Post, isLiked: Boolean) = viewModelScope.launch(Dispatchers.IO) {
//        try {
//            val postUpdated = postRepo.updateLikePost(oldPost, isLiked)
//            if (postUpdated != null) {
//                updateListPost(oldPost, postUpdated)
//            } else {
//                updateListPost(oldPost, oldPost)
//                throw Exception("Error")
//            }
//        } catch (e: Exception) {
//            Timber.d("Excepcion al dar like ${e.message}")
//            _messagePost.send("Erro al dar like")
//            if (e is CancellationException) throw e
//        }
//    }

//    private fun updateListPost(oldPost: Post, newPost: Post) {
//        val currentMyPost = _listMyPost.value
//        if (currentMyPost is Resource.Success) {
//            updateInfoIfNeeded(oldPost, newPost, currentMyPost.data)?.let {
//                _listMyPost.value = Resource.Success(it)
//            }
//        }
//        val currentLastPost = _listPost.value
//        if (currentLastPost is Resource.Success) {
//            updateInfoIfNeeded(oldPost, newPost, currentLastPost.data)?.let {
//                _listPost.value = Resource.Success(it)
//            }
//        }
//    }

    private fun updateInfoIfNeeded(oldPost: Post, newPost: Post, list: List<Post>): List<Post>? {
        // ! use this and no use index of
        val index = list.indexOfFirst { oldPost.id == newPost.id }
        val mutableList = list.toMutableList()
        return if (index != -1) {
            mutableList[index] = newPost
            mutableList
        } else {
            null
        }

    }
}