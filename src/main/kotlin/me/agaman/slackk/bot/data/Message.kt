package me.agaman.slackk.bot.data

data class Message(
        val type: String,
        val user: String?,
        val text: String,
        val ts: String,
        val attachments: List<Attachment>?
)
