package me.agaman.slackk.bot

import kotlinx.coroutines.runBlocking
import me.agaman.slackk.bot.helper.Serializer
import me.agaman.slackk.bot.impl.ApiClient
import me.agaman.slackk.bot.request.base.FormRequest
import me.agaman.slackk.bot.request.base.Request
import me.agaman.slackk.bot.result.Result
import kotlin.reflect.KClass

class BotClient(
        token: String
) {
    private val apiClient = ApiClient(token)

    suspend inline fun <reified T : Any> send(request: Request<T>): Result<T> = send(request, T::class)

    @PublishedApi
    internal suspend fun <T : Any> send(request: Request<T>, resultClass: KClass<T>): Result<T> {
        val result = if (request is FormRequest<T>) {
            apiClient.callForm(request.requestMethod(), request.formData())
        } else {
            apiClient.call(request.requestMethod(), Serializer.toJson(request))
        }

        val resultStatus = Serializer.fromJson<ResultStatus>(result)
        return if (resultStatus.ok) {
            Result.success(Serializer.fromJson(result, resultClass.java))
        } else {
            Result.error(resultStatus.error)
        }
    }

    private data class ResultStatus(
            val ok: Boolean,
            val error: String
    )
}
