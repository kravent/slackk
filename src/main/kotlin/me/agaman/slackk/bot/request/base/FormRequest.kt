package me.agaman.slackk.bot.request.base

abstract class FormRequest<ResultClass : Any>(
        requestMethod: String
) : Request<ResultClass>(requestMethod) {
    abstract fun formData(): Map<String, String?>
}
