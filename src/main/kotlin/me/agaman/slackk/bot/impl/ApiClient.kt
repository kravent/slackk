package me.agaman.slackk.bot.impl

import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody

class ApiClient(
        private val token: String
) {
    private val httpClient = OkHttpClient()

    fun call(method: String, jsonData: String = ""): String {
        val url = "https://slack.com/api/$method"
        val request = Request.Builder()
                .url(url)
                .header("Authorization", "Bearer $token")
                .post(RequestBody.create(MediaType.parse("application/json"), jsonData))
                .build()
        return httpClient.newCall(request)
                .execute()
                .body()!!
                .string()
    }
}
