package me.agaman.slackk.bot.data

import com.google.gson.annotations.SerializedName

data class User(
        val id: String,
        val name: String,
        @SerializedName("real_name")
        val realName: String,
        @SerializedName("team_id")
        val teamId: String,
        val color: String,
        @SerializedName("is_bot")
        val isBot: Boolean

)
