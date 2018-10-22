package me.agaman.slackk.bot.request

import me.agaman.slackk.bot.request.base.FormRequest
import me.agaman.slackk.bot.result.UserProfileGetResult

data class UserProfileGetRequest (
        val user: String,
        val includeLabels: Boolean? = null
) : FormRequest<UserProfileGetResult>("users.profile.get") {
    override fun formData(): Map<String, String?> = mapOf(
            "user" to user,
            "include_labels" to includeLabels.toString()
    )
}
