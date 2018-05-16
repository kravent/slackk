package me.agaman.slackk.bot.event

data class MessageEvent(
        val channel: String,
        val user: String?,
        val text: String,
        val ts: String,
        val source_team: String,
        val team: String
) : Event()
