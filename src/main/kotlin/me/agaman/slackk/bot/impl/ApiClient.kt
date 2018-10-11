package me.agaman.slackk.bot.impl

import org.http4k.client.ApacheClient
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.body.form

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

    fun callForm(method: String, data: Map<String, String>): String {
        var request = Request(Method.POST, "https://slack.com/api/$method")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .form("token", token)
        data.forEach { key, value -> request = request.form(key, value) }
        return httpClient(request)
                .bodyString()
    }
}
