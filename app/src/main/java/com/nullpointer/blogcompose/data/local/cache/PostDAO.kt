package com.nullpointer.blogcompose.data.local.cache

import androidx.room.*
import com.nullpointer.blogcompose.models.Post
import kotlinx.coroutines.flow.Flow

@Dao
interface PostDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: Post)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertListPost(list: List<Post>)

    @Update
    suspend fun updatePost(post: Post)

    @Query("DELETE FROM table_post")
    suspend fun deleterAll()

    @Query("SELECT * FROM table_post ORDER BY timeStamp DESC")
     fun getAllPost(): Flow<List<Post>>

    @Query("SELECT * FROM table_post WHERE id = :idPost")
    suspend fun getPostById(idPost: String): Post?

    @Query("SELECT * FROM table_post ORDER BY timeStamp DESC LIMIT 1")
    suspend fun getFirstPost():Post?

    @Query("SELECT * FROM table_post ORDER BY timeStamp LIMIT 1")
    suspend fun getLastPost():Post?

    @Query("SELECT * FROM table_post WHERE uuid = :idUser")
    suspend fun getPostByUser(idUser: String): List<Post>

}