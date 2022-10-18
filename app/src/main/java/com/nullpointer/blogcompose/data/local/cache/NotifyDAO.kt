package com.nullpointer.blogcompose.data.local.cache

import androidx.room.*
import com.nullpointer.blogcompose.models.notify.Notify
import kotlinx.coroutines.flow.Flow

@Dao
interface NotifyDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotify(notify: Notify)

    @Update
    suspend fun updateNotify(notify: Notify)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertListNotify(list: List<Notify>)

    @Transaction
    suspend fun updateAllNotify(list: List<Notify>) {
        deleterAll()
        insertListNotify(list)
    }

    @Query("DELETE FROM table_notify")
    suspend fun deleterAll()

    @Query("SELECT * FROM table_notify ORDER BY timestamp DESC")
    fun getAllNotify(): Flow<List<Notify>>

    @Query("SELECT * FROM table_notify ORDER BY timestamp DESC LIMIT 1")
    suspend fun getFirstNotify(): Notify?

    @Query("SELECT * FROM table_notify ORDER BY timestamp ASC LIMIT 1")
    suspend fun getLastNotify(): Notify?

}