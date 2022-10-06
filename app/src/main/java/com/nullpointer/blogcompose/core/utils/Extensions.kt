package com.nullpointer.blogcompose.core.utils

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Parcel
import android.text.format.DateFormat.is24HourFormat
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.compose.AsyncImagePainter
import com.google.firebase.firestore.DocumentSnapshot
import com.valentinilk.shimmer.Shimmer
import com.valentinilk.shimmer.shimmer
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

fun Long.toFormat(context: Context): String {
    val newPattern = if (is24HourFormat(context)) {
        "dd MMM hh:mm"
    } else {
        "dd MMM hh:mm a"
    }
    val sdf = SimpleDateFormat(newPattern, Locale.getDefault())
    return sdf.format(this)
}

val Context.correctFlag:Int get() {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    } else {
        PendingIntent.FLAG_UPDATE_CURRENT
    }
}

fun Date?.toFormat(context: Context): String {
    val pattern = "EEEE dd/MM/yyyy HH:mm:ss".let {
        if (is24HourFormat(context)) it else it.plus(" aa")
    }
    val format = SimpleDateFormat(pattern, Locale.getDefault())
    val dateTimeNow = this ?: Date()
    return format.format(dateTimeNow)
}

fun Context.getNotifyServices(): NotificationManager {
    return getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
}

fun Context.showToastMessage(@StringRes stringRes: Int) {
    Toast.makeText(this, getString(stringRes), Toast.LENGTH_SHORT).show()
}

fun Context.sendEmail(email:String){
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("mailto:$email"))
    startActivity(intent)
}

@Composable
inline fun <reified VM : ViewModel> shareViewModel():VM {
    val activity = LocalContext.current as ComponentActivity
    return hiltViewModel(activity)
}

fun DocumentSnapshot.timestampEstimate(nameField: String): Date? {
    return getTimestamp(nameField, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)?.toDate()
}

fun Parcel.writeDate(date: Date?) {
    writeLong(date?.time ?: -1)
}

fun Parcel.readDate(): Date? {
    val long = readLong()
    return if (long != -1L) Date(long) else null
}


val AsyncImagePainter.isSuccess get() = state is AsyncImagePainter.State.Success

@Composable
fun getGrayColor(): Color {
    return (if (isSystemInDarkTheme()) Color.LightGray else Color.DarkGray)
}

fun Modifier.myShimmer(
    shimmer: Shimmer,
): Modifier = composed {
    shimmer(shimmer).background(getGrayColor())
}


fun ViewModel.launchSafeIO(
    blockBefore: suspend CoroutineScope.() -> Unit = {},
    blockAfter: suspend CoroutineScope.(Boolean) -> Unit = {},
    blockException: suspend CoroutineScope.(Exception) -> Unit = {},
    blockIO: suspend CoroutineScope.() -> Unit,
): Job {
    var isForCancelled = false
    return viewModelScope.launch {
        try {
            blockBefore()
            withContext(Dispatchers.IO) { blockIO() }
        } catch (e: Exception) {
            when (e) {
                is CancellationException -> {
                    isForCancelled = true
                    throw e
                }
                else -> blockException(e)
            }
        } finally {
            blockAfter(isForCancelled)
        }
    }
}