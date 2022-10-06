package com.nullpointer.blogcompose.data.local.post

import com.nullpointer.blogcompose.data.local.cache.MyPostDAO
import com.nullpointer.blogcompose.data.local.cache.PostDAO
import com.nullpointer.blogcompose.models.posts.MyPost
import com.nullpointer.blogcompose.models.posts.Post
import com.nullpointer.blogcompose.models.posts.SimplePost
import kotlinx.coroutines.flow.Flow

class PostLocalDataSourceImpl(
    private val postDAO: PostDAO,
    private val myPostDAO: MyPostDAO
) : PostLocalDataSource {
    override val listLastPost: Flow<List<Post>> = postDAO.getAllPost()
    override val listMyLastPost: Flow<List<MyPost>> = myPostDAO.getAllPost()

    override suspend fun getFirstPost(): Post? =
        postDAO.getFirstPost()

    override suspend fun getMyFirstPost(): MyPost? =
        myPostDAO.getFirstPost()

    override suspend fun updatePost(post: SimplePost) {
        (post as? MyPost)?.let {
            myPostDAO.updatePost(it)
        }
        (post as? Post)?.let {
            postDAO.updatePost(it)
        }

    }


    override suspend fun getLastMyPost(): MyPost? =
        myPostDAO.getLastPost()

    override suspend fun getLastPost(): Post? =
        postDAO.getLastPost()

    override suspend fun updateAllPost(listPost: List<Post>) =
        postDAO.updateAllPost(listPost)

    override suspend fun updateAllMyPost(listMyPost: List<MyPost>) =
        myPostDAO.updateAllPost(listMyPost)

    override suspend fun insertListPost(listPost: List<Post>) =
        postDAO.insertListPost(listPost)

    override suspend fun insertListMyPost(listMyPost: List<MyPost>) =
        myPostDAO.insertListPost(listMyPost)

}