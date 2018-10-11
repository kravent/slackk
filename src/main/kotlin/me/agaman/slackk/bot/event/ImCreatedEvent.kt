package me.agaman.slackk.bot.event

import com.google.gson.annotations.SerializedName

@EventType("im_created")
data class ImCreatedEvent(
        val user: String,
        val event_ts: String,
        val channel: Channel
) : Event() {
    data class Channel(
        val id: String,
        val user: String,
        val created: Int,
        @SerializedName("is_im")
        val isIm: Boolean,
        @SerializedName("is_org_shared")
        val isOrgShared: Boolean,
        @SerializedName("last_read")
        val lastRead: String,
        //val latest: XXXX,
        @SerializedName("unread_count")
        val unreadCount: Int,
        @SerializedName("unread_count_display")
        val unreadCountDisplay: Int,
        @SerializedName("is_open")
        val isOpen: Boolean
    )
}
