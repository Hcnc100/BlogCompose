package com.nullpointer.blogcompose.domain.post

import com.nullpointer.blogcompose.core.utils.callApiTimeOut
import com.nullpointer.blogcompose.data.local.post.PostLocalDataSource
import com.nullpointer.blogcompose.data.local.preferences.PreferencesDataSource
import com.nullpointer.blogcompose.data.remote.post.PostRemoteDataSource
import com.nullpointer.blogcompose.models.notify.Notify
import com.nullpointer.blogcompose.models.notify.TypeNotify
import com.nullpointer.blogcompose.models.posts.MyPost
import com.nullpointer.blogcompose.models.posts.Post
import com.nullpointer.blogcompose.models.posts.SimplePost
import com.nullpointer.blogcompose.models.users.AuthUser
import com.nullpointer.blogcompose.models.users.SimpleUser
import kotlinx.coroutines.flow.Flow

@Suppress("UNCHECKED_CAST")
class PostRepoImpl(
    private val prefDataSource: PreferencesDataSource,
    private val postLocalDataSource: PostLocalDataSource,
    private val postRemoteDataSource: PostRemoteDataSource
) : PostRepository {

    companion object {
        private const val SIZE_POST_REQUEST = 5L
        private const val SIZE_MY_POST_REQUEST = 7L
    }

    override val listLastPost: Flow<List<Post>> = postLocalDataSource.listLastPost
    override val listMyLastPost: Flow<List<MyPost>> = postLocalDataSource.listMyLastPost


    override suspend fun requestLastPost(forceRefresh: Boolean): Int {
        return callApiTimeOut {
            val firstPost = if (forceRefresh) null else postLocalDataSource.getFirstPost()
            val listLastPost = postRemoteDataSource.getLastPost(
                numberPost = SIZE_POST_REQUEST,
                idPost = firstPost?.id
            )
            postLocalDataSource.updateAllPost(listLastPost)
            listLastPost.size
        }
    }


    override suspend fun requestMyLastPost(forceRefresh: Boolean): Int {
        return callApiTimeOut {
            val firstPost = if (forceRefresh) null else postLocalDataSource.getMyFirstPost()
            val listMyLastPost = postRemoteDataSource.getLastPost(
                idPost = firstPost?.id,
                fromUserId = prefDataSource.getIdUser(),
                numberPost = SIZE_MY_POST_REQUEST
            )

            postLocalDataSource.updateAllMyPost(listMyLastPost.map { it.toMyPost() })
            listMyLastPost.size
        }
    }

    override suspend fun concatenatePost(): Int {
        return callApiTimeOut {
            postLocalDataSource.getLastPost()?.let { lastPost ->
                val concatenatePost = postRemoteDataSource.getConcatenatePost(
                    idPost = lastPost.id,
                    numberPosts = SIZE_POST_REQUEST
                )
                postLocalDataSource.insertListPost(concatenatePost)
                concatenatePost.size
            }
            0
        }
    }

    override suspend fun concatenateMyPost(): Int {
        return callApiTimeOut {
            postLocalDataSource.getLastPost()?.let { lastPost ->
                val concatenatePost = postRemoteDataSource.getConcatenatePost(
                    idPost = lastPost.id,
                    fromUserId = prefDataSource.getIdUser(),
                    numberPosts = SIZE_MY_POST_REQUEST
                )
                postLocalDataSource.insertListMyPost(concatenatePost.map { it.toMyPost() })
                concatenatePost.size
            }
            0
        }

    }


    override suspend fun updatePostById(idPost: String) {
        callApiTimeOut {
            postRemoteDataSource.getPost(idPost)?.let {
                postLocalDataSource.updatePost(it)
            }
        }
    }

    override suspend fun updatePost(post: SimplePost) {
        postLocalDataSource.updatePost(post)
    }


    override suspend fun updateLikePost(post: SimplePost, isLiked: Boolean) {
        callApiTimeOut {
            // * toggle like post
            val postFakeUpdate = post.toggleLike()

            // * update fake post
            postLocalDataSource.updatePost(postFakeUpdate)

            // * create notify if is needed
            val currentUser = prefDataSource.getCurrentUser()
            val newNotify = if (post.userPoster?.idUser != prefDataSource.getIdUser() && isLiked)
                post.createLikeNotify(currentUser) else null

            // * update post and send notification
            postRemoteDataSource.updateLikes(
                idPost = post.id,
                isLiked = !post.ownerLike,
                notify = newNotify,
                ownerPost = post.userPoster?.idUser!!,
                idUser = currentUser.id
            )?.let {
                postLocalDataSource.updatePost(it)
            }
        }
    }


    override suspend fun getRealTimePost(idPost: String): Flow<Post?> {
        return callApiTimeOut {
            postRemoteDataSource.getRealTimePost(idPost)
        }
    }


    override suspend fun addNewPost(post: Post) {
        callApiTimeOut {
            val idPost = postRemoteDataSource.addNewPost(post)
            postRemoteDataSource.getLastPost(
                idPost = idPost,
                includePost = true,
                numberPost = SIZE_POST_REQUEST
            ).let { listPost ->
                postLocalDataSource.updateAllPost(listPost)
                postLocalDataSource.updateAllMyPost(listPost.map { it.toMyPost() })
            }
        }
    }


    private fun SimplePost.createLikeNotify(myUser: AuthUser): Notify {
        return Notify(
            userInNotify = SimpleUser(
                idUser = myUser.id,
                name = myUser.name,
                urlImg = myUser.urlImg
            ),
            idPost = id,
            urlImgPost = urlImage,
            type = TypeNotify.LIKE
        )
    }

    private fun SimplePost.toggleLike(): SimplePost {
        this as Post
        val newCount = if (ownerLike) numberLikes - 1 else numberLikes + 1
        return this.copy(
            numberLikes = newCount,
            ownerLike = !ownerLike
        )
    }

    private fun Post.toMyPost(): MyPost {
        return MyPost(
            description = description,
            userPoster = userPoster,
            urlImage = urlImage,
            numberComments = numberComments,
            numberLikes = numberLikes,
            ownerLike = ownerLike,
            timestamp = timestamp,
            id = id,
        )
    }

}