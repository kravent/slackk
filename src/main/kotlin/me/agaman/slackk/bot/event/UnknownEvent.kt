package me.agaman.slackk.bot.event

data class UnknownEvent(
        val type: String,
        val jsonData: String
) : Event()
