package me.agaman.slackk.bot.result

data class RtmConnectResult(
        val ok: Boolean,
        val url: String,
        val self: Self
) {
    data class Self(
            val id: String,
            val name: String
    )
}
