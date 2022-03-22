package com.nullpointer.blogcompose.ui.screens.details.viewModel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nullpointer.blogcompose.core.delegates.SavableProperty
import com.nullpointer.blogcompose.core.states.Resource
import com.nullpointer.blogcompose.domain.post.PostRepoImpl
import com.nullpointer.blogcompose.models.Comment
import com.nullpointer.blogcompose.models.Post
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class PostDetailsViewModel @Inject constructor(
    private val postRepoImpl: PostRepoImpl,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {


    init {
        viewModelScope.launch {
            postRepoImpl.clearComments()
        }
    }

    private val _idPost = MutableStateFlow("")
    private val _hasNewComments = MutableStateFlow(false)
    val hasNewComments = _hasNewComments.asStateFlow()
    var numberComments by SavableProperty(savedStateHandle, "KEY_COMMENTS", -1)
        private set
    val postState: StateFlow<Resource<Post>> = flow {
        _idPost.collect { idPost ->
            if (idPost.isNotEmpty()) {
                postRepoImpl.getRealTimePost(idPost).collect {
                    emit(Resource.Success(it!!))
                    postRepoImpl.updateInnerPost(it)
                    if (it.numberComments != numberComments) {
                        if (numberComments != -1) _hasNewComments.value = true
                        numberComments = it.numberComments
                    }
                }
            }
        }
    }.catch { c: Throwable ->
        Timber.d("Error con el post ")
        Resource.Failure<Post>(Exception(c))
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        Resource.Loading()
    )

    val commentState: StateFlow<Resource<List<Comment>>> = flow {
        postRepoImpl.listComments.collect {
            emit(Resource.Success(it))
        }
    }.catch {
        Resource.Failure<List<Comment>>(Exception(it))
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        Resource.Loading()
    )

    fun concatenateComments() = viewModelScope.launch {
        postRepoImpl.concatenateComments(_idPost.value)
    }


    fun addComment(idPost: String, comment: String) = viewModelScope.launch {
        try {
            numberComments++
            postRepoImpl.addNewComment(idPost, comment)
        } catch (e: Exception) {
            Timber.e("Error al agregar un commet")
        }
    }

    fun initIdPost(idPost: String) = viewModelScope.launch {
        _idPost.value = idPost
        postRepoImpl.getLastComments(idPost)
    }

    fun reloadNewComment() = viewModelScope.launch {
        postRepoImpl.getLastComments(_idPost.value)
        _hasNewComments.value = false
    }


}