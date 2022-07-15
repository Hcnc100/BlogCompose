package com.nullpointer.blogcompose.data.local.cache

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.nullpointer.blogcompose.models.Comment
import com.nullpointer.blogcompose.models.posts.MyPost
import com.nullpointer.blogcompose.models.notify.Notify
import com.nullpointer.blogcompose.models.posts.Post

@Database(entities = [Post::class, Notify::class, MyPost::class],
    version = 1,
    exportSchema = false)
@TypeConverters(Converts::class)
abstract class BlogDataBase : RoomDatabase() {
    companion object {
        const val BLOG_DATABASE = "BLOG_DATABASE"
    }

    abstract fun getNotifyDAO(): NotifyDAO
    abstract fun getPostDAO(): PostDAO
    abstract fun getMyPostDAO(): MyPostDAO
}