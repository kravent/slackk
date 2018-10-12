package me.agaman.slackk.bot.request

import me.agaman.slackk.bot.request.base.Request
import me.agaman.slackk.bot.result.ConversationHistoryResult

data class ConversationHistoryRequest(
        val channel: String,
        val inclusive: Boolean? = null,
        val latest: String? = null,
        val limit: Int? = null,
        val oldest: String? = null
) : Request<ConversationHistoryResult>("conversations.history")
