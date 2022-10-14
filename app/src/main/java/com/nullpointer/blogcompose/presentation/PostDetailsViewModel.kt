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
import com.nullpointer.blogcompose.core.utils.ExceptionManager.sendMessageErrorToException
import com.nullpointer.blogcompose.core.utils.launchSafeIO
import com.nullpointer.blogcompose.domain.comment.CommentsRepository
import com.nullpointer.blogcompose.domain.post.PostRepository
import com.nullpointer.blogcompose.models.Comment
import com.nullpointer.blogcompose.models.posts.Post
import com.nullpointer.blogcompose.models.posts.SimplePost
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class PostDetailsViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val commentsRepository: CommentsRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    companion object {
        private const val KEY_COMMENTS = "KEY_COMMENTS"
        private const val TAG_COMMENT_DETAILS="TAG_COMMENT_DETAILS"
    }

    // * this var is for send any messaging
    private val _messageDetails = Channel<String>()
    val messageDetails = _messageDetails.receiveAsFlow()


    var isConcatenateComment by mutableStateOf(false)


    // * this var is for update post selected
    private val _idPost = MutableStateFlow("")


    // * show that has any comments
    var hasNewComments by mutableStateOf(false)
        private set

    var addingComment by mutableStateOf(false)
        private set

    // * var to saved number of comments
    var numberComments by SavableProperty(savedStateHandle, KEY_COMMENTS, -1)
        private set

    private val _listComments = MutableStateFlow<Resource<List<Comment>>>(Resource.Loading)
    val listComments = _listComments.asStateFlow()

    var currentPost: SimplePost? = null

    val comment = PropertySavableString(
        savedState = savedStateHandle,
        label = R.string.label_comment,
        hint = R.string.comment_hint,
        maxLength = 250,
        lengthError = R.string.error_length_comment,
        tagSavable = TAG_COMMENT_DETAILS
    )

    val postState: StateFlow<Resource<Post>> = _idPost
        .filter { it.isNotEmpty() }
        .transform<String, Resource<Post>> {
            postRepository.getRealTimePost(it).collect { updatedPost ->
                requireNotNull(updatedPost)

                if (updatedPost.numberComments != numberComments) {
                    if (numberComments != -1 && updatedPost.numberComments > numberComments) {
                        hasNewComments = true
                    }
                    numberComments = updatedPost.numberComments
                }

                emit(Resource.Success(updatedPost))

                postRepository.updatePost(updatedPost)

            }
        }.flowOn(Dispatchers.IO).catch {
            sendMessageErrorToException(
                exception = it,
                message = "Error get post real time",
                channel = _messageDetails
            )
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
        _idPost.value = idPost
        requestsComments(idPost)
    }


    fun concatenateComments() = launchSafeIO(
        isEnabled = !isConcatenateComment,
        blockBefore = { isConcatenateComment = true },
        blockAfter = { isConcatenateComment = false },
        blockIO = {
            (listComments.value as? Resource.Success)?.let { stateListComment ->
                val lastComment = stateListComment.data.first()
                val listNewComments = commentsRepository.concatenateComments(
                    idPost = _idPost.value,
                    lastComment = lastComment.id
                )
                val newList = listNewComments + stateListComment.data
                _listComments.emit(Resource.Success(newList))
                Timber.d("New comments concatenate ${listNewComments.size}")
            }
        },
        blockException = {
            sendMessageErrorToException(
                exception = it,
                message = "Error al concatenar los post ${_idPost.value}",
                _messageDetails
            )
        }
    )


    fun addComment(
        callbackSuccess: () -> Unit
    ) = launchSafeIO(
        blockBefore = { addingComment = true },
        blockAfter = { addingComment = false },
        blockIO = {
            (postState.value as? Resource.Success)?.let { statePost ->
                val newComment = comment.currentValue
                comment.clearValue()
                numberComments++
                val listNewComment = commentsRepository.addNewComment(
                    post = statePost.data,
                    comment = newComment
                )
                _listComments.emit(Resource.Success(listNewComment))
                callbackSuccess()
            }
        },
        blockException = {
            sendMessageErrorToException(
                exception = it,
                message = "Error reload comments ${_idPost.value}",
                channel = _messageDetails
            )
        }
    )


    fun requestsComments(idPost: String = _idPost.value) = launchSafeIO(
        blockException = {
            sendMessageErrorToException(
                exception = it,
                message = "Error al recargar comentarios ${_idPost.value}",
                channel = _messageDetails
            )
        },
        blockIO = {
            withContext(Dispatchers.Main) { hasNewComments = false }
            val listNewComments = commentsRepository.getLastComments(idPost)
            _listComments.emit(Resource.Success(listNewComments))
        }
    )

    override fun onCleared() {
        _idPost.value = ""
        numberComments = -1
    }


}