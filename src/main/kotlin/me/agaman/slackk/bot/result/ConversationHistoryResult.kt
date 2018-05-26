package me.agaman.slackk.bot.result

import com.google.gson.annotations.SerializedName
import me.agaman.slackk.bot.data.Message

data class ConversationHistoryResult(
        val messages: List<Message>,
        @SerializedName("has_more")
        val hasMore: Boolean
)
