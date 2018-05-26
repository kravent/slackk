package me.agaman.slackk.bot.impl

import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.newSingleThreadContext
import mu.KotlinLogging

internal object Coroutines {
    private const val CALLBACK_ERROR_MESSAGE = "Error thrown in Slackk callback"

    private val slackkCoroutineContext = newSingleThreadContext("slackk")
    private val logger = KotlinLogging.logger {}

    inline fun wrapCallback(crossinline job: () -> Unit) = {
        launch(slackkCoroutineContext) {
            try {
                job()
            } catch (t: Throwable) {
                logger.error(t) { CALLBACK_ERROR_MESSAGE }
            }
        }
    }

    inline fun <reified T> wrapCallback(crossinline job: (T) -> Unit) = { param: T ->
        launch(slackkCoroutineContext) {
            try {
                job(param)
            } catch (t: Throwable) {
                logger.error(t) { CALLBACK_ERROR_MESSAGE }
            }
        }
    }
}

