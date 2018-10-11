package me.agaman.slackk.bot

import com.google.gson.Gson
import me.agaman.slackk.bot.impl.ApiClient
import me.agaman.slackk.bot.request.FormRequest
import me.agaman.slackk.bot.request.Request
import me.agaman.slackk.bot.result.Result
import kotlin.reflect.KClass

class BotClient(
        token: String
) {
    companion object {
        private val gson = Gson()
    }

    private val apiClient = ApiClient(token)

    inline fun <reified T : Any> send(request: Request<T>) : Result<T> = send(request, T::class)

    @PublishedApi
    internal fun <T : Any> send(request: Request<T>, resultClass: KClass<T>) : Result<T> {
        val result = if (request is FormRequest<T>) {
            apiClient.callForm(request.requestMethod(), request.formData().filterValues { it != null } as Map<String, String>)
        } else {
            apiClient.call(request.requestMethod(), gson.toJson(request))
        }

        val resultStatus = gson.fromJson(result, ResultStatus::class.java)
        return if (resultStatus.ok) {
            Result.success(gson.fromJson(result, resultClass.java))
        } else {
            Result.error(resultStatus.error)
        }
    }

    private data class ResultStatus(
            val ok: Boolean,
            val error: String
    )
}
