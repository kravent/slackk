package me.agaman.slackk.bot.request

import com.google.gson.annotations.SerializedName
import me.agaman.slackk.bot.request.base.Request
import me.agaman.slackk.bot.result.UserProfileGetResult

data class UserProfileGetRequest (
        val user: String,
        @SerializedName("include_labels")
        val includeLabels: Boolean? = null
) : Request<UserProfileGetResult>("users.profile.get")
