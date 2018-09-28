package me.agaman.slackk.bot.data

import com.google.gson.annotations.SerializedName

data class UserProfile(
        @SerializedName("status_text")
        val statusText: String?,
        @SerializedName("status_emoji")
        val statusEmoji: String?,
        @SerializedName("real_name")
        val realName: String,
        @SerializedName("display_name")
        val displayName: String,
        @SerializedName("real_name_normalized")
        val realNameNormalized: String,
        @SerializedName("display_name_normalized")
        val displayNameNormalized: String,
        val email: String?
)
