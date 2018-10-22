package me.agaman.slackk.bot.request

import me.agaman.slackk.bot.request.base.FormRequest
import me.agaman.slackk.bot.result.UserInfoResult

data class UserInfoRequest(
        val user: String,
        val includeLocale: Boolean? = null
) : FormRequest<UserInfoResult>("users.info") {
    override fun formData(): Map<String, String?> = mapOf(
            "user" to user,
            "include_locale" to includeLocale.toString()
    )
}
