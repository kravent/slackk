package me.agaman.slackk.bot.request

import com.google.gson.annotations.SerializedName
import me.agaman.slackk.bot.result.FileUploadResult

data class FileUploadRequest(
        val content: String,
        val title: String? = null,
        val channels: String? = null,
        val filename: String? = null,
        val filetype: String? = null,
        @SerializedName("initial_comment")
        val initialComment: String? = null,
        @SerializedName("thread_ts")
        val threadTs: String? = null
) : Request<FileUploadResult>("files.upload")
