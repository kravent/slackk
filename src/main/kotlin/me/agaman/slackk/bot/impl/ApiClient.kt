package me.agaman.slackk.bot.impl

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.http.ContentType
import io.ktor.http.Parameters
import io.ktor.http.content.TextContent
import io.ktor.http.contentType


internal class ApiClient(
        private val token: String
) {
    private val client = HttpClient(CIO)

    suspend fun call(slackMethod: String, jsonData: String = ""): String =
            client.post("https://slack.com/api/$slackMethod") {
                header("Authorization", "Bearer $token")
                body = TextContent(jsonData, ContentType.Application.Json)
            }

    suspend fun callForm(slackMethod: String, data: Map<String, String?>): String =
            client.post("https://slack.com/api/$slackMethod") {
                body = FormDataContent(Parameters.build {
                    append("token", token)
                    data.forEach { key, value -> value?.also { append(key, value) } }
                })
            }
}
