package com.nullpointer.blogcompose.ui.screens.details.viewModel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nullpointer.blogcompose.core.delegates.SavableProperty
import com.nullpointer.blogcompose.core.states.Resource
import com.nullpointer.blogcompose.core.utils.NetworkException
import com.nullpointer.blogcompose.domain.post.PostRepoImpl
import com.nullpointer.blogcompose.models.Comment
import com.nullpointer.blogcompose.models.Post
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
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

    companion object {
        const val KEY_COMMENTS = "KEY_COMMENTS"
    }

    // * job to control init comments and emit state
    private var jobInitComments: Job? = null
    private val _stateInitComments = MutableStateFlow<Resource<Unit>?>(null)
    val stateInitComments = _stateInitComments.asStateFlow()

    // * save job to concatenate comments
    private var jobConcatenate: Job? = null
    private val _stateConcatenate = MutableStateFlow<Resource<Unit>?>(null)
    val stateConcatenate = _stateConcatenate.asStateFlow()

    // * this var is for send any messaging
    private val _messageDetails = Channel<String>()
    val messageDetails = _messageDetails.receiveAsFlow()

    // * this var is for update post selected
    private val _idPost = MutableStateFlow("")

    // * show that has any comments
    private val _hasNewComments = MutableStateFlow(false)
    val hasNewComments = _hasNewComments.asStateFlow()

    // * var to saved number of comments
    var numberComments by SavableProperty(savedStateHandle, KEY_COMMENTS, -1)
        private set

    init {
        // * every when lauch this view model deleter comments saved in room
        viewModelScope.launch {
            postRepoImpl.clearComments()
        }
    }

    val postState: StateFlow<Resource<Post>> = flow {
        // * update the comments when idPost is updated and this is not empty
        _idPost.collect { idPost ->
            if (idPost.isNotEmpty()) {
                // ! only listener one document
                // * and request new comments for demand
                postRepoImpl.getRealTimePost(idPost).collect {
                    // * emit post reciber
                    emit(Resource.Success(it!!))
                    // * update inner post (saved in database)
                    postRepoImpl.updateInnerPost(it)
                    // * if the number of comments is diff and no if for me
                    // * so, there are more comments
                    if (it.numberComments != numberComments) {
                        if (numberComments != -1) _hasNewComments.value = true
                        numberComments = it.numberComments
                    }
                }
            }
        }
    }.catch {
        Timber.d("Error con el post $it")
        Resource.Failure<Post>(Exception(it))
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        Resource.Loading()
    )

    // * this show commnets saved in database
    // * the update is for demand and only update database
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

    // * set id post and request comments
    fun initIdPost(idPost: String) {
        _idPost.value = idPost
        reloadNewComment()
    }

    fun concatenateComments() {
        // * cancel old job and add new
        jobConcatenate?.cancel()
        jobConcatenate = viewModelScope.launch {
            _stateConcatenate.value = Resource.Loading()
            _stateConcatenate.value = try {
                // * request new commnets and concatenate in database
                postRepoImpl.concatenateComments(_idPost.value)
                Resource.Success(Unit)
            } catch (e: Exception) {
                when (e) {
                    is CancellationException -> throw e
                    is NetworkException -> _messageDetails.send("No hay conexion a intenet")
                    else -> {
                        _messageDetails.send("Error desconocido")
                        Timber.d("Error al concatenar los post ${_idPost.value} : $e")
                    }
                }
                Resource.Failure(e)
            }
        }
    }


    fun addComment(idPost: String, comment: String) = viewModelScope.launch {
        try {
            // * change number of comments
            // ! this for no show any for "hasNewComments"
            numberComments++
            postRepoImpl.addNewComment(idPost, comment)
        } catch (e: Exception) {
            _messageDetails.send("No se puedo agregar el comentario")
            Timber.e("Error al agregar un commet $e")
        }
    }


    fun reloadNewComment() {
        // * this override comments in database and get last comments
        jobInitComments?.cancel()
        jobInitComments = viewModelScope.launch {
            _stateInitComments.value = Resource.Loading()
            _stateInitComments.value = try {
                postRepoImpl.getLastComments(_idPost.value)
                _hasNewComments.value = false
                Resource.Success(Unit)
            } catch (e: Exception) {
                when (e) {
                    is CancellationException -> throw e
                    is NetworkException -> _messageDetails.send("No hay conexion a internet")
                    else -> {
                        _messageDetails.send("Error desconocido")
                        Timber.e("Error al recargar comentarios ${_idPost.value} : $e")
                    }
                }
                Resource.Failure(e)
            }
        }
    }


}