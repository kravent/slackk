package me.agaman.slackk.bot.event

data class ImCreatedEvent(
        val user: String,
        val event_ts: String,
        val channel: Channel
) : Event() {
    data class Channel(
        val id: String,
        val user: String,
        val created: Int,
        val is_im: Boolean,
        val is_org_shared: Boolean,
        val last_read: String,
        //val latest: XXXX,
        val unread_count: Int,
        val unread_count_display: Int,
        val is_open: Boolean
    )
}