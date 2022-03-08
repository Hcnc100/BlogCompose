package com.nullpointer.blogcompose.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nullpointer.blogcompose.core.delegates.SavableProperty
import com.nullpointer.blogcompose.core.states.Resource
import com.nullpointer.blogcompose.domain.post.PostRepoImpl
import com.nullpointer.blogcompose.domain.preferences.PreferencesRepoImpl
import com.nullpointer.blogcompose.models.Post
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(
    private val postRepo: PostRepoImpl
) : ViewModel() {


    private val _listPost = MutableStateFlow<Resource<List<Post>>>(Resource.Loading())
    val listPost = _listPost.asStateFlow()

    private val _listMyPost = MutableStateFlow<Resource<List<Post>>>(Resource.Loading())
    val listMyPost = _listMyPost.asStateFlow()

    private val _messagePost = Channel<String>()
    val messagePost = _messagePost.receiveAsFlow()

    init {
        fetchLastPost()
        fetchMyLastPost()
    }

    fun fetchLastPost() = viewModelScope.launch(Dispatchers.IO) {
        _listPost.value = Resource.Loading()
        _listPost.value = try {
            Resource.Success(postRepo.getLastPost(false))
        } catch (e: Exception) {
            Timber.e(e)
            Resource.Failure(e)
        }
    }

    fun fetchMyLastPost() = viewModelScope.launch {
        _listMyPost.value = Resource.Loading()
        _listMyPost.value = try {
            Resource.Success(postRepo.getMyLastPost("",false))
        } catch (e: Exception) {
            Resource.Failure(e)
        }
    }

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



    fun likePost(oldPost: Post, isLiked: Boolean) = viewModelScope.launch(Dispatchers.IO) {
        try {
            val postUpdated = postRepo.updateLikePost(oldPost, isLiked)
            if (postUpdated != null) {
                updateListPost(oldPost, postUpdated)
            } else {
                updateListPost(oldPost, oldPost)
                throw Exception("Error")
            }
        } catch (e: Exception) {
            Timber.d("Excepcion al dar like ${e.message}")
            _messagePost.send("Erro al dar like")
            if (e is CancellationException) throw e
        }
    }

    private fun updateListPost(oldPost: Post, newPost: Post) {
        val currentMyPost = _listMyPost.value
        if (currentMyPost is Resource.Success) {
            updateInfoIfNeeded(oldPost, newPost, currentMyPost.data)?.let {
                _listMyPost.value = Resource.Success(it)
            }
        }
        val currentLastPost = _listPost.value
        if (currentLastPost is Resource.Success) {
            updateInfoIfNeeded(oldPost, newPost, currentLastPost.data)?.let {
                _listPost.value = Resource.Success(it)
            }
        }
    }

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