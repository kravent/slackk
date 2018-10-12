package me.agaman.slackk.bot.request

import com.google.gson.annotations.SerializedName
import me.agaman.slackk.bot.request.base.Request
import me.agaman.slackk.bot.result.UserListResult

data class UserListRequest(
        val cursor: String? = null,
        val limit: Int? = null,
        @SerializedName("include_locale")
        val includeLocale: Boolean? = null
) : Request<UserListResult>("users.list")
