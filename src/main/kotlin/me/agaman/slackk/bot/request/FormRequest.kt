package me.agaman.slackk.bot.request

abstract class FormRequest<ResultClass : Any>(
        requestMethod: String
) : Request<ResultClass>(requestMethod) {
    internal abstract fun formData(): Map<String, String?>
}
