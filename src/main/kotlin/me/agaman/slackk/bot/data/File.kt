package me.agaman.slackk.bot.data

import com.google.gson.annotations.SerializedName

data class File(
        val id: String,
        val name: String? = null,
        val title: String? = null,
        val mimetype: String? = null,
        val filetype: String? = null,
        val user: String? = null,
        val channels: Set<String> = emptySet(),
        @SerializedName("comments_count")
        val commentsCount: Int = 0,
        val shares: Shares = Shares()
)
