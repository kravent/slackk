package me.agaman.slackk.bot.request

// TODO avoid serializing this fields
open class Request<ResultClass : Any>(
        @Transient
        private val _requestMethod: String
) {
    fun requestMethod() = _requestMethod
}
