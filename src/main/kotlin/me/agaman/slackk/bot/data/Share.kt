package me.agaman.slackk.bot.data

import com.google.gson.annotations.SerializedName

data class Share(
        val ts: String,
        @SerializedName("thread_ts")
        val threadTs: String? = null,
        @SerializedName("reply_count")
        val replyCount: Int = 0,
        @SerializedName("reply_users_count")
        val replyUsersCount: Int = 0,
        @SerializedName("reply_users")
        val replyUsers: Set<String> = emptySet(),
        @SerializedName("latest_reply")
        val latestReply: String? = null,
        @SerializedName("channel_name")
        val channelName: String? = null,
        @SerializedName("team_id")
        val teamId: String? = null
)
