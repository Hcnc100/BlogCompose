package com.nullpointer.blogcompose.presentation

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nullpointer.blogcompose.core.delegates.SavableProperty
import com.nullpointer.blogcompose.core.delegates.SaveableComposeState
import com.nullpointer.blogcompose.core.states.Resource
import com.nullpointer.blogcompose.domain.post.PostRepoImpl
import com.nullpointer.blogcompose.models.Post
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(
    private val postRepo: PostRepoImpl,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    companion object {
        const val INIT_POST_LOAD = 5
        const val NEW_POST_REQUEST = 10
        const val KEY_ENABLE_LAST = "KEY_ENABLE_LAST"
        const val KEY_ENABLE_MYSELF = "KEY_ENABLE_MYSELF"
        const val KEY_SIZE_LAST = "KEY_LAST_SIZE"
        const val KEY_SIZE_MYSELF = "KEY_SIZE_MYSELF"
    }

    private val _listPost = MutableStateFlow<Resource<List<Post>>>(Resource.Loading())
    val listPost = _listPost.asStateFlow()

    private val _listMyPost = MutableStateFlow<Resource<List<Post>>>(Resource.Loading())
    val listMyPost = _listMyPost.asStateFlow()

    private var enableGetLastPost by SavableProperty(savedStateHandle, KEY_ENABLE_LAST, true)
    private var currentSizeLastPost by SavableProperty(savedStateHandle,
        KEY_SIZE_LAST,
        INIT_POST_LOAD)

    private var enableGetMyPost by SavableProperty(savedStateHandle, KEY_ENABLE_MYSELF, true)
    private var currentSizeMyPost by SavableProperty(savedStateHandle,
        KEY_SIZE_MYSELF,
        INIT_POST_LOAD)

    init {
        fetchLastPost()
        fetchMyLastPost()
    }

    fun fetchLastPost() = viewModelScope.launch(Dispatchers.IO) {
        _listPost.value = Resource.Loading()
        _listPost.value = try {
            Resource.Success(
                postRepo.getLastPost(currentSizeLastPost)
                    .also { currentSizeLastPost = it.size }
            )
        } catch (e: Exception) {
            Resource.Failure(e)
        }
    }

    fun fetchMyLastPost() = viewModelScope.launch {
        _listMyPost.value = Resource.Loading()
        _listMyPost.value = try {
            val sizeEstimated = currentSizeLastPost
            Resource.Success(
                postRepo.getMyLastPost(sizeEstimated)
                    .also { currentSizeMyPost = it.size }
            )
        } catch (e: Exception) {
            Resource.Failure(e)
        }
    }

    fun concatenateLastPost() = viewModelScope.launch(Dispatchers.IO) {
        try {
            if (enableGetLastPost) {
                val sizeEstimated = currentSizeLastPost + NEW_POST_REQUEST
                val listNewPost = postRepo.getLastPost(sizeEstimated)
                currentSizeLastPost = listNewPost.size
                _listPost.value = Resource.Success(listNewPost)
                if (listNewPost.size < sizeEstimated) {
                    enableGetLastPost = false
                    Timber.d("Limite alcalzado se desactivo la carga infinita de todos")
                }
            }
        } catch (e: Exception) {
            Timber.d("Excepcion con la carga concatedana ${e.message}")
            if (e is CancellationException) throw e
        }
    }

    fun concatenateMyPost() = viewModelScope.launch(Dispatchers.IO) {
        try {
            if (enableGetMyPost) {
                val sizeEstimated = currentSizeMyPost + NEW_POST_REQUEST
                val listNewPost = postRepo.getMyLastPost(sizeEstimated)
                currentSizeMyPost = listNewPost.size
                _listMyPost.value = Resource.Success(listNewPost)
                if (listNewPost.size < sizeEstimated) {
                    enableGetMyPost = false
                    Timber.d("Limite alcalzado se desactivo la carga infinita de mis post")
                }

            }
        } catch (e: Exception) {
            Timber.d("Excepcion con la carga concatedana ${e.message}")
            if (e is CancellationException) throw e
        }
    }
}