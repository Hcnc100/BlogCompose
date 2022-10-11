package com.nullpointer.blogcompose.models.customSnack

import com.nullpointer.blogcompose.models.enums.TypeSnackBar
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class MessageSnack(
    val msgResource: Int,
    val type: TypeSnackBar
) {


    companion object {
        fun decode(value: String): MessageSnack {
            return Json.decodeFromString(value)
        }

        fun encode(messageSnack: MessageSnack): String {
            return Json.encodeToString(messageSnack)
        }

        fun createMessageEncode(resourceStr: Int, type: TypeSnackBar): String {
            return encode(MessageSnack(resourceStr, type))
        }

        fun createErrorMessageEncode(resourceStr: Int): String =
            encode(MessageSnack(resourceStr, TypeSnackBar.ERROR))

        fun createSuccessMessageEncode(resourceStr: Int): String =
            encode(MessageSnack(resourceStr, TypeSnackBar.SUCCESS))

        fun createWarningMessageEncode(resourceStr: Int): String =
            encode(MessageSnack(resourceStr, TypeSnackBar.WARNING))

        fun createInfoMessageEncode(resourceStr: Int): String =
            encode(MessageSnack(resourceStr, TypeSnackBar.INFO))

        fun createRetryMessageEncode(resourceStr: Int): String =
            encode(MessageSnack(resourceStr, TypeSnackBar.RETRY))
    }


}
