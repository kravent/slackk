package me.agaman.slackk.bot.impl

import kotlinx.coroutines.experimental.CoroutineName
import kotlinx.coroutines.experimental.launch
import mu.KotlinLogging

private const val ERROR_MESSAGE = "Error thrown in Slackk callback"

private val slackkCoroutineContext = CoroutineName("slackk")
private val logger = KotlinLogging.logger {}

internal inline fun wrapSlackkCallback(crossinline job: () -> Unit) = {
    launch(slackkCoroutineContext) {
        try {
            job()
        } catch (t: Throwable) {
            logger.error(t) { ERROR_MESSAGE }
        }
    }
}

internal inline fun <reified T> wrapSlackkCallback(crossinline job: (T) -> Unit) = { param: T ->
    launch(slackkCoroutineContext) {
        try {
            job(param)
        } catch (t: Throwable) {
            logger.error(t) { ERROR_MESSAGE }
        }
    }
}
