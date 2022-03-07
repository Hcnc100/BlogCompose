package com.nullpointer.blogcompose.core.utils

import android.content.Context
import android.text.format.DateFormat
import java.text.SimpleDateFormat
import java.util.*

fun Long.toFormat(context: Context): String {
    val newPattern = if (DateFormat.is24HourFormat(context)) {
        "dd MMM hh:mm"
    } else {
        "dd MMM hh:mm a"
    }
    val sdf = SimpleDateFormat(newPattern, Locale.getDefault())
    return sdf.format(this)
}
