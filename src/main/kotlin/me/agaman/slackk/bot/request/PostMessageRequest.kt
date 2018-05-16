package me.agaman.slackk.bot.request

import com.google.gson.annotations.SerializedName
import me.agaman.slackk.bot.result.PostMessageResult

data class PostMessageRequest(
        val channel: String,
        val text: String,
        @SerializedName("as_user") val asUser: Boolean? = null,
        val mrkdwn: Boolean? = null
) : Request<PostMessageResult>("chat.postMessage")
