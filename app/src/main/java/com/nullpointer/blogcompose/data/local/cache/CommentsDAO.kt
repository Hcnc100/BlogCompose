package com.nullpointer.blogcompose.data.local.cache

import androidx.room.*
import com.nullpointer.blogcompose.models.Comment
import kotlinx.coroutines.flow.Flow

@Dao
interface CommentsDAO {
        @Insert(onConflict = OnConflictStrategy.REPLACE)
        suspend fun insertComment(comment: Comment)

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        suspend fun insertListComments(list: List<Comment>)


        @Query("DELETE FROM table_comments")
        suspend fun deleterAll()

        @Transaction
        suspend fun updateAllComments(list: List<Comment>) {
            deleterAll()
            insertListComments(list)
        }

        @Query("SELECT * FROM table_comments ORDER BY timeStamp DESC")
        fun getAllComments(): Flow<List<Comment>>

        @Query("SELECT * FROM table_comments ORDER BY timeStamp DESC LIMIT 1")
        suspend fun getFirstComment(): Comment?

        @Query("SELECT * FROM table_comments ORDER BY timeStamp LIMIT 1")
        suspend fun getLastComment(): Comment?
    }
