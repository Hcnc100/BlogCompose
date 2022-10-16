package com.nullpointer.blogcompose.domain.post

import com.nullpointer.blogcompose.core.utils.callApiTimeOut
import com.nullpointer.blogcompose.data.local.post.PostLocalDataSource
import com.nullpointer.blogcompose.data.local.preferences.PreferencesDataSource
import com.nullpointer.blogcompose.data.remote.post.PostRemoteDataSource
import com.nullpointer.blogcompose.models.notify.Notify
import com.nullpointer.blogcompose.models.notify.TypeNotify
import com.nullpointer.blogcompose.models.posts.MyPost
import com.nullpointer.blogcompose.models.posts.Post
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
        val firstPost = if (forceRefresh) null else postLocalDataSource.getFirstPost()
        val listLastPost = callApiTimeOut {
            postRemoteDataSource.getLastPost(
                numberPost = SIZE_POST_REQUEST,
                idPost = firstPost?.id
            )
        }
        postLocalDataSource.updateAllPost(listLastPost)
        return listLastPost.size
    }


    override suspend fun requestMyLastPost(forceRefresh: Boolean): Int {
        val firstPost = if (forceRefresh) null else postLocalDataSource.getMyFirstPost()
        val idUser = prefDataSource.getIdUser()
        val listMyLastPost = callApiTimeOut {
            postRemoteDataSource.getLastPost(
                idPost = firstPost?.id,
                fromUserId = idUser,
                numberPost = SIZE_MY_POST_REQUEST
            )
        }
        postLocalDataSource.updateAllMyPost(listMyLastPost.map { it.toMyPost() })
        return listMyLastPost.size
    }

    override suspend fun concatenatePost(): Int {
        val lastPost = postLocalDataSource.getLastPost()
        return if (lastPost != null) {
            val listConcatenate = callApiTimeOut {
                postRemoteDataSource.getConcatenatePost(
                    idPost = lastPost.id,
                    numberPosts = SIZE_POST_REQUEST
                )
            }
            postLocalDataSource.insertListPost(listConcatenate)
            listConcatenate.size
        } else {
            0
        }
    }

    override suspend fun concatenateMyPost(): Int {
        val lastMyPost = postLocalDataSource.getLastMyPost()

        return if (lastMyPost != null) {
            val concatenatePost = callApiTimeOut {
                postRemoteDataSource.getConcatenatePost(
                    idPost = lastMyPost.id,
                    fromUserId = prefDataSource.getIdUser(),
                    numberPosts = SIZE_MY_POST_REQUEST
                )
            }
            postLocalDataSource.insertListMyPost(concatenatePost.map { it.toMyPost() })
            concatenatePost.size
        } else {
            0
        }

    }


    override suspend fun updatePostById(idPost: String) {
        callApiTimeOut {
            postRemoteDataSource.getPost(idPost)
        }?.let { post ->
            postLocalDataSource.updatePost(post, post.toMyPost())
        }

    }

    override suspend fun updatePost(post: Post) {
        postLocalDataSource.updatePost(post, post.toMyPost())
    }


    override suspend fun updateLikePost(post: Post, isLiked: Boolean) {
        // * toggle like post
        val postFakeUpdate = post.toggleLike()

        // * update fake post
        postLocalDataSource.updatePost(postFakeUpdate, postFakeUpdate.toMyPost())

        // * create notify if is needed
        val currentUser = prefDataSource.getCurrentUser()

        val newNotify = if (post.userPoster?.idUser != prefDataSource.getIdUser() && isLiked)
            post.createLikeNotify(currentUser) else null

        callApiTimeOut {
            // * update post and send notification
            postRemoteDataSource.updateLikes(
                idPost = post.id,
                isLiked = !post.ownerLike,
                notify = newNotify,
                ownerPost = post.userPoster?.idUser!!,
                idUser = currentUser.id
            )
        }?.let { it ->
            postLocalDataSource.updatePost(post = it, myPost = it.toMyPost())
        }
    }


    override suspend fun getRealTimePost(idPost: String): Flow<Post?> {
        return callApiTimeOut {
            postRemoteDataSource.getRealTimePost(idPost)
        }
    }


    override suspend fun addNewPost(post: Post) {
        val myIdUser = prefDataSource.getIdUser()
        val idLastPost = postLocalDataSource.getMyFirstPost()?.id
        val newIdPost = postRemoteDataSource.addNewPost(post)

        callApiTimeOut {
            postRemoteDataSource.getLastPost(
                idPost = newIdPost,
                includePost = true,
                numberPost = SIZE_POST_REQUEST
            )
        }.let { listPost ->
            postLocalDataSource.updateAllPost(listPost)
        }

        callApiTimeOut {
            postRemoteDataSource.getLastPostBetween(
                fromUserId = myIdUser,
                startWithId = newIdPost,
                endWithId = idLastPost
            )
        }.let { listPost ->
            postLocalDataSource.insertListMyPost(listPost.map { it.toMyPost() })
        }

    }


    private fun Post.createLikeNotify(myUser: AuthUser): Notify {
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

    private fun Post.toggleLike(): Post {
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