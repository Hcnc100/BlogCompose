package com.nullpointer.blogcompose.data.local.cache

import androidx.room.TypeConverter
import com.nullpointer.blogcompose.models.notify.TypeNotify
import java.util.*

class Converts {
    @TypeConverter
    fun fromDate(date: Date?): Long = date?.time ?: 0

    @TypeConverter
    fun toDate(long: Long): Date = Date(long)

    @TypeConverter
    fun fromType(type: TypeNotify):String = type.name

    @TypeConverter
    fun toType(value:String):TypeNotify = TypeNotify.valueOf(value)
}