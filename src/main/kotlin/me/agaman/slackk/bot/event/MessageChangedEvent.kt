package me.agaman.slackk.bot.event

import com.google.gson.annotations.SerializedName
import me.agaman.slackk.bot.data.Message

data class MessageChangedEvent(
        val subtype: String?,
        val channel: String,
        val ts: String,
        val message: Message,
        @SerializedName("previous_message")
        val previousMessage: Message
) : Event()
