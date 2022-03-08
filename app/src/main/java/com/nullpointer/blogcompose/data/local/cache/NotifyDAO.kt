package com.nullpointer.blogcompose.data.local.cache

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nullpointer.blogcompose.models.Notify

@Dao
interface NotifyDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotify(notify: Notify)

    @Query("DELETE FROM table_notify")
    suspend fun deleterAll()
}