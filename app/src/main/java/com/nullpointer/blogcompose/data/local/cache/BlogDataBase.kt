package com.nullpointer.blogcompose.data.local.cache

import androidx.room.Database
import androidx.room.RoomDatabase
import com.nullpointer.blogcompose.models.Notify
import com.nullpointer.blogcompose.models.Post

@Database(entities = [Post::class, Notify::class], version = 1, exportSchema = false)
abstract class BlogDataBase : RoomDatabase() {
    companion object{
        const val BLOG_DATABASE="BLOG_DATABASE"
    }

    abstract fun getNotifyDAO(): NotifyDAO
    abstract fun getPostDAO(): PostDAO
}