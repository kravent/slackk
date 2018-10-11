package me.agaman.slackk.bot.request

import me.agaman.slackk.bot.result.FileUploadResult

data class FileUploadRequest(
        val content: String,
        val title: String? = null,
        val channels: String? = null,
        val filename: String? = null,
        val filetype: String? = null,
        val initialComment: String? = null,
        val threadTs: String? = null
) : FormRequest<FileUploadResult>("files.upload") {
    override fun formData(): Map<String, String?> = mapOf(
            "content" to content,
            "title" to title,
            "channels" to channels,
            "filename" to filename,
            "filetype" to filetype,
            "initial_comment" to initialComment,
            "thread_ts" to threadTs
    )
}
