package me.agaman.slackk.bot.request

import com.google.gson.annotations.SerializedName
import me.agaman.slackk.bot.request.base.Request
import me.agaman.slackk.bot.result.UserInfoResult

data class UserInfoRequest(
        val user: String,
        @SerializedName("include_locale")
        val includeLocale: Boolean? = null
) : Request<UserInfoResult>("users.info")
