package com.nullpointer.blogcompose.core.utils

import android.content.Context
import android.text.format.DateUtils.*
import com.nullpointer.blogcompose.R
import java.util.concurrent.TimeUnit

object TimeUtils {
    fun getTimeAgo(time: Long,context: Context): String {
        val timeNow = System.currentTimeMillis()
        if (time > timeNow || time <= 0) {
            return context.getString(R.string.text_time_future)
        }
        val diff = timeNow - time
        return when {
            diff < MINUTE_IN_MILLIS -> context.getString(R.string.text_time_just_now)
            diff < 2 * MINUTE_IN_MILLIS -> context.getString(R.string.text_time_one_minute_ago)
            diff < 60 * MINUTE_IN_MILLIS -> context.getString(R.string.text_time_more_minutes_ago,diff / MINUTE_IN_MILLIS)
            diff < 2 * HOUR_IN_MILLIS -> context.getString(R.string.text_time_one_hour_ago)
            diff < 24 * HOUR_IN_MILLIS -> context.getString(R.string.text_time_more_hour_ago,diff / HOUR_IN_MILLIS)
            diff < 48 * HOUR_IN_MILLIS -> context.getString(R.string.text_time_yesterday)
            else -> context.getString(R.string.text_time_days_Ago,diff / DAY_IN_MILLIS)
        }
    }
}
