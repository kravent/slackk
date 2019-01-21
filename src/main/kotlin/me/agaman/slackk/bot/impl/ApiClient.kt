package me.agaman.slackk.bot.impl

import org.http4k.client.ApacheClient
import org.http4k.core.Method
import org.http4k.core.Request

internal class ApiClient(
        private val token: String
) {
    private val httpClient = ApacheClient()

    fun call(method: String, jsonData: String = ""): String {
        val request = Request(Method.POST, "https://slack.com/api/$method")
                .header("Authorization", "Bearer $token")
                .header("Content-Type", "application/json")
                .body(jsonData)
        return httpClient(request)
                .bodyString()
    }
}
