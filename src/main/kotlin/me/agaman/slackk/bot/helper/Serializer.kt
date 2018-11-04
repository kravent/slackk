package me.agaman.slackk.bot.helper

import com.google.gson.GsonBuilder

internal object Serializer {
    private val gson = GsonBuilder().create()

    inline fun <reified T : Any> fromJson(json: String) = fromJson(json, T::class.java)
    fun <T : Any> fromJson(json: String, type: Class<T>) = gson.fromJson(json, type)

    fun <T : Any> toJson(element: T) = gson.toJson(element)
}
