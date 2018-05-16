package me.agaman.slackk.bot

import com.google.gson.Gson
import me.agaman.slackk.bot.impl.ApiClient
import me.agaman.slackk.bot.request.Request
import kotlin.reflect.KClass

private val gson = Gson()

class BotClient(
        token: String
) {
    private val apiClient = ApiClient(token)

    inline fun <reified T : Any> send(request: Request<T>) : T = send(request, T::class)

    @PublishedApi
    internal fun <T : Any> send(request: Request<T>, resultClass: KClass<T>) : T {
        val result = apiClient.call(request.requestMethod(), gson.toJson(request))
        return gson.fromJson(result, resultClass.java)
    }
}
