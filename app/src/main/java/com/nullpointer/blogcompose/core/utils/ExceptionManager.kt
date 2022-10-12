package com.nullpointer.blogcompose.core.utils

import com.nullpointer.blogcompose.R
import com.nullpointer.blogcompose.models.customSnack.MessageSnack
import kotlinx.coroutines.channels.Channel
import timber.log.Timber

object ExceptionManager {
    const val NO_INTERNET_CONNECTION = "NO_INTERNET_NETWORK"
    const val SERVER_TIME_OUT = "SERVER_TIME_OUT"

    private fun getMessageForMsgException(exception: Exception, message: String?): String {
        Timber.e("${message}: $exception")
        return when (exception.message) {
            NO_INTERNET_CONNECTION -> MessageSnack.createInfoMessageEncode(R.string.message_error_internet_checker)
            SERVER_TIME_OUT -> MessageSnack.createRetryMessageEncode(R.string.error_time_out)
            else -> {
//                crash.recordException(exception)
                MessageSnack.createErrorMessageEncode(R.string.message_error_unknown)
            }
        }
    }


    fun sendMessageErrorToException(
        exception: Exception,
        message: String,
        channel: Channel<String>
    ) {
        val messageUser = getMessageForMsgException(exception, message)
        channel.trySend(messageUser)
    }
}