package me.agaman.slackk.bot.event

import com.google.gson.annotations.SerializedName

@EventType("message")
data class MessageEvent(
        val channel: String,
        val user: String,
        val text: String,
        val ts: String,
        @SerializedName("source_team")
        val sourceTeam: String,
        val team: String
) : Event()
