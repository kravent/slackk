package me.agaman.slackk.bot.request

import com.google.gson.annotations.SerializedName
import me.agaman.slackk.bot.result.UserProfileResult

data class UserProfileGet (
        val user: String,
        @SerializedName("include_labels")
        val includeLabels: Boolean? = null
) : Request<UserProfileResult>("users.profile.get")
