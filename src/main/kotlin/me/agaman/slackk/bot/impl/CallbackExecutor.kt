package me.agaman.slackk.bot.impl

import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.newSingleThreadContext
import mu.KotlinLogging

internal object CallbackExecutor {
    private val slackkCoroutineContext = newSingleThreadContext("slackk")
    private val logger = KotlinLogging.logger {}

    inline fun wrapCallback(crossinline job: () -> Unit) = { runCallback(job) }
    inline fun <reified T> wrapCallback(crossinline job: (T) -> Unit) = { param: T -> runCallback { job(param) } }

    inline fun runCallback(crossinline job: () -> Unit) = launch(slackkCoroutineContext) {
        try {
            job()
        } catch (t: Throwable) {
            logger.error(t) { "Error thrown in Slackk callback" }
        }
    }
}

