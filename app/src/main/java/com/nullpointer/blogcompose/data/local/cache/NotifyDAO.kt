package com.nullpointer.blogcompose.data.local.cache

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nullpointer.blogcompose.models.Notify

interface NotifyDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotify(notify: Notify)

    @Query("DELETE FROM table_notify")
    suspend fun deleterAll()
}