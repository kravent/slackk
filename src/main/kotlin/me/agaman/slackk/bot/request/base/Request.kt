package me.agaman.slackk.bot.request.base

open class Request<ResultClass : Any>(
        @Transient
        private val _requestMethod: String
) {
    fun requestMethod() = _requestMethod
}
