package me.agaman.slackk.bot.result

data class PostMessageResult(
        val ok: Boolean,
        val channel: String,
        val ts: String
)
