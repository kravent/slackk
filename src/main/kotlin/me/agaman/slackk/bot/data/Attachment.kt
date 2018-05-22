package me.agaman.slackk.bot.data

import com.google.gson.annotations.SerializedName

data class Attachment(
        val fallback: String? = null,
        val color: String? = null,
        val title: String? = null,
        @SerializedName("title_link")
        val titleLink: String? = null,
        val text: String? = null,
        val pretext: String? = null,
        val fields: List<AttachmentField>? = null
)
