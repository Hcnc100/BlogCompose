package com.nullpointer.blogcompose.ui.screens.details.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
) : ViewModel() {

    private val _idPost = MutableStateFlow("")

    val postState: StateFlow<Resource<Post>> = flow {
        _idPost.collect { idPost ->
            if (idPost.isNotEmpty()) {
                postRepoImpl.getRealTimePost(idPost).collect {
                    emit(Resource.Success(it!!))
                    postRepoImpl.updateInnerPost(it)
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
        _idPost.collect { idPost ->
            if (idPost.isNotEmpty()) {
                postRepoImpl.getCommetsRealTime(idPost).collect {
                    emit(Resource.Success(it))
                }
            }
        }
    }.catch { c: Throwable ->
        Timber.d("Error al colectar los comentarios $c ")
        Resource.Failure<List<Comment>>(Exception(c))
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        Resource.Loading()
    )

    fun addComment(idPost: String, comment: String,callBack:()->Unit) = viewModelScope.launch {
        postRepoImpl.addNewComment(idPost, comment)
        callBack()
    }

    fun initIdPost(idPost: String) {
        _idPost.value = idPost
    }

}