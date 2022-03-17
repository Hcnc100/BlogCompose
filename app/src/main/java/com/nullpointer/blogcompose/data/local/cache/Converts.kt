package com.nullpointer.blogcompose.data.local.cache

import androidx.room.TypeConverter
import java.util.*

class Converts {
    @TypeConverter
    fun fromDate(date: Date?): Long = date?.time ?: 0

    @TypeConverter
    fun toDate(long: Long): Date = Date(long)
}