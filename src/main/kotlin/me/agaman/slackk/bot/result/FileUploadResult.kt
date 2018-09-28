package me.agaman.slackk.bot.result

import me.agaman.slackk.bot.data.File

data class FileUploadResult(
        val ok: Boolean,
        val error: String? = null,
        val file: File? = null
)
