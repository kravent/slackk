package me.agaman.slackk.bot.event

import com.google.gson.annotations.SerializedName
import me.agaman.slackk.bot.data.Message

@EventType("message")
data class MessageEvent(
        val subtype: String?,
        val channel: String,
        val user: String,
        val text: String,
        val ts: String,
        val source_team: String,
        val team: String,
        val message: Message,
        @SerializedName("previous_message")
        val previousMessage: Message
) : Event()
