package com.nullpointer.blogcompose.data.local.cache

import androidx.room.*
import com.nullpointer.blogcompose.models.Post
import com.nullpointer.blogcompose.models.MyPost
import kotlinx.coroutines.flow.Flow

@Dao
interface MyPostDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: MyPost)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertListPost(list: List<MyPost>)

    @Query("SELECT * FROM table_my_post WHERE id = :idPost")
    suspend fun getPostById(idPost: String): Post?

    @Update
    suspend fun updatePost(post: MyPost)

    @Query("DELETE FROM table_my_post")
    suspend fun deleterAll()

    @Query("SELECT * FROM table_my_post ORDER BY timeStamp DESC")
    fun getAllPost(): Flow<List<Post>>

    @Query("SELECT * FROM table_post ORDER BY timeStamp DESC LIMIT 1")
    suspend fun getFirstPost(): Post?

    @Query("SELECT * FROM table_post ORDER BY timeStamp LIMIT 1")
    suspend fun getLastPost(): Post?

    @Query("SELECT EXISTS(SELECT * FROM table_my_post WHERE id = :idPost)")
    fun isPostExist(idPost : String) : Boolean
}