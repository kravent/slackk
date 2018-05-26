package me.agaman.slackk.bot.impl

import mu.KotlinLogging

private const val ERROR_MESSAGE = "Slackk: Error thrown in listener"

private val logger = KotlinLogging.logger {}

internal inline fun addErrorHandler(crossinline job: () -> Unit) = {
    try {
        job()
    } catch (t: Throwable) {
        logger.error(t) { ERROR_MESSAGE }
    }
}

internal inline fun <reified T> addErrorHandler(crossinline job: (T) -> Unit) = { param: T ->
    try {
        job(param)
    } catch (t: Throwable) {
        logger.error(t) { ERROR_MESSAGE }
    }
}
