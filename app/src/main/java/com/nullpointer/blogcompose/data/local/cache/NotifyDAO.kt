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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertListNotify(list: List<Notify>)

    @Query("DELETE FROM table_notify")
    suspend fun deleterAll()

    @Query("SELECT * FROM table_notify")
    suspend fun getAllNotify(): List<Notify>
}