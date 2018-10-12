package me.agaman.slackk.bot.request

import me.agaman.slackk.bot.request.base.Request
import me.agaman.slackk.bot.result.ApiTestResult

data class ApiTestRequest(
        val error: String? = null,
        val foo: String? = null
) : Request<ApiTestResult>("api.test")
