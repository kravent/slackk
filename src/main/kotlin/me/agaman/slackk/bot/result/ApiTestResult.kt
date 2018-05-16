package me.agaman.slackk.bot.result

data class ApiTestResult(
        val ok: Boolean,
        val error: String?,
        val args: Map<String, Any>?
)
