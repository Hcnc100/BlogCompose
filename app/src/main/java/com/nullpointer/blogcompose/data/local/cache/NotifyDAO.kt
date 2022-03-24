package com.nullpointer.blogcompose.data.local.cache

import androidx.room.*
import com.nullpointer.blogcompose.models.Notify
import kotlinx.coroutines.flow.Flow

@Dao
interface NotifyDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotify(notify: Notify)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertListNotify(list: List<Notify>)

    @Transaction
    suspend fun updateAllNotify(list: List<Notify>) {
        deleterAll()
        insertListNotify(list)
    }

    @Transaction
    suspend fun updateListPost(numberNotify: Int, list: List<Notify>) {
        if (list.size == numberNotify) {
            updateAllNotify(list)
        } else {
            val retainPost = list + getListFirstPost(numberNotify - list.size)
            updateAllNotify(retainPost)
        }
    }

    @Query("SELECT * FROM table_notify ORDER BY timestamp DESC LIMIT :numberNotify")
    suspend fun getListFirstPost(numberNotify: Int): List<Notify>

    @Query("DELETE FROM table_notify")
    suspend fun deleterAll()

    @Query("SELECT * FROM table_notify")
    fun getAllNotify(): Flow<List<Notify>>

    @Query("SELECT * FROM table_notify ORDER BY timestamp DESC LIMIT 1")
    suspend fun getFirstNotify(): Notify?

    @Query("SELECT * FROM table_notify ORDER BY timestamp LIMIT 1")
    suspend fun getLastNotify(): Notify?

}