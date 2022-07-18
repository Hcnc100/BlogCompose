package com.nullpointer.blogcompose.models.notify

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.nullpointer.blogcompose.models.users.SimpleUser
import java.lang.reflect.Type

class NotifyDeserializer : JsonDeserializer<Notify?> {
    @Throws(JsonParseException::class)
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type?,
        context: JsonDeserializationContext?,
    ): Notify {
        val jsonObject = json.asJsonObject
        val userObject=jsonObject["userInNotify"].asJsonObject
        val userInNotify = SimpleUser(
            idUser = userObject["idUser"].asString,
            urlImg = userObject["urlImg"].asString,
            name = userObject["name"].asString,
        )
        return Notify(
            id = jsonObject["id"].asString,
            idPost = jsonObject["idPost"].asString,
            urlImgPost = jsonObject["urlImgPost"].asString,
            type = TypeNotify.valueOf( jsonObject["type"].asString),
            userInNotify = userInNotify
        )
    }
}