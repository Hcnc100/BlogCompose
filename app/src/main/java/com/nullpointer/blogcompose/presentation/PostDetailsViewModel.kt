package com.nullpointer.blogcompose.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nullpointer.blogcompose.R
import com.nullpointer.blogcompose.core.delegates.PropertySavableString
import com.nullpointer.blogcompose.core.delegates.SavableProperty
import com.nullpointer.blogcompose.core.states.Resource
import com.nullpointer.blogcompose.core.utils.NetworkException
import com.nullpointer.blogcompose.domain.comment.CommentsRepository
import com.nullpointer.blogcompose.domain.post.PostRepoImpl
import com.nullpointer.blogcompose.domain.post.PostRepository
import com.nullpointer.blogcompose.models.Comment
import com.nullpointer.blogcompose.models.posts.Post
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class PostDetailsViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val commentsRepository: CommentsRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    companion object {
        const val KEY_COMMENTS = "KEY_COMMENTS"
    }

    // * job to control init comments and emit state
    private var jobInitComments: Job? = null

    // * this var is for send any messaging
    private val _messageDetails = Channel<Int>()
    val messageDetails = _messageDetails.receiveAsFlow()

    // * save job to concatenate comments
    private var jobConcatenate: Job? = null

    var stateConcatComment by mutableStateOf(false)


    // * this var is for update post selected
    private val _idPost = MutableStateFlow("")

    // * show that has any comments
    var hasNewComments by mutableStateOf(false)
        private set

    // * var to saved number of comments
    var numberComments by SavableProperty(savedStateHandle, KEY_COMMENTS, -1)
        private set

    private val _listComments = MutableStateFlow<Resource<List<Comment>>>(Resource.Loading)
    val listComments = _listComments.asStateFlow()

    val comment = PropertySavableString(
        state = savedStateHandle,
        label = R.string.label_comment,
        hint = R.string.comment_hint,
        maxLength = 250,
        lengthError = R.string.error_length_comment
    )

    val postState: StateFlow<Resource<Post>> = flow<Resource<Post>> {
        // * update the comments when idPost is updated and this is not empty
        _idPost.collect {
            postRepository.getRealTimePost(it).collect { newPost ->
                if (newPost!!.numberComments != numberComments) {
                    if (numberComments != -1 && newPost.numberComments > numberComments)
                        hasNewComments = true
                    numberComments = newPost.numberComments
                }

                // * emit post reciber
                emit(Resource.Success(newPost))
                // * update inner post (saved in database)
                withContext(Dispatchers.IO) {
                    postRepository.updatePost(newPost)
                }
            }
        }
    }.flowOn(Dispatchers.IO).catch {
        Timber.d("Error con el post $it")
        _messageDetails.send(R.string.message_error_load_post)
        postRepository.deleterPost(_idPost.value)
        emit(Resource.Failure)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        Resource.Loading
    )

    // * this show commnets saved in database
    // * the update is for demand and only update database


    // * set id post and request comments
    fun initIdPost(idPost: String) {
        if (idPost != _idPost.value) {
            _idPost.value = idPost
            requestsComments(idPost)
        }
    }


    fun concatenateComments() {
        // * cancel old job and add new
        jobConcatenate?.cancel()
        jobConcatenate = viewModelScope.launch {
            stateConcatComment = true
            try {
                listComments.value.let { stateListComment ->
                    if (stateListComment is Resource.Success) {
                        val lastComment = stateListComment.data.first()
                        val listNewComments = withContext(Dispatchers.IO) {
                            commentsRepository.concatenateComments(_idPost.value, lastComment.id)
                        }
                        val newList = listNewComments + stateListComment.data
                        _listComments.emit(Resource.Success(newList))
                        Timber.d("New comments concatenate ${listNewComments.size}")
                    }
                }
            } catch (e: Exception) {
                when (e) {
                    is CancellationException -> throw e
                    is NetworkException -> _messageDetails.send(R.string.message_error_internet_checker)
                    else -> {
                        _messageDetails.send(R.string.message_error_unknown)
                        Timber.d("Error al concatenar los post ${_idPost.value} : $e")
                    }
                }
            } finally {
                stateConcatComment = false
            }
        }
    }


    fun addComment(comment: String) = viewModelScope.launch {
        try {
            // * change number of comments
            // ! this for no show any for "hasNewComments"
            numberComments++
            postState.value.let {
                if (it is Resource.Success) {
                    val listNewComment = withContext(Dispatchers.IO) {
                        commentsRepository.addNewComment(it.data, comment)
                    }
                    _listComments.emit(Resource.Success(listNewComment))
                }
            }
        } catch (e: Exception) {
            _messageDetails.send(R.string.message_error_add_comment)
            Timber.e("Error al agregar un commet $e")
        }
    }


    fun requestsComments(idPost: String = _idPost.value) {
        // * this override comments in database and get last comments
        jobInitComments?.cancel()
        jobInitComments = viewModelScope.launch {
            try {
                hasNewComments = false
                _listComments.emit(Resource.Loading)
                val listNewComments = withContext(Dispatchers.IO) {
                    commentsRepository.getLastComments(idPost)
                }
                _listComments.emit(Resource.Success(listNewComments))
            } catch (e: Exception) {
                _listComments.emit(Resource.Failure)
                when (e) {
                    is CancellationException -> throw e
                    is NetworkException -> _messageDetails.send(R.string.message_error_internet_checker)
                    else -> {
                        _messageDetails.send(R.string.message_error_load_comments)
                        Timber.e("Error al recargar comentarios ${_idPost.value} : $e")
                    }
                }
            }
        }
    }

    override fun onCleared() {
        _idPost.value = ""
        numberComments = -1
        jobConcatenate?.cancel()
        jobInitComments?.cancel()
    }


}