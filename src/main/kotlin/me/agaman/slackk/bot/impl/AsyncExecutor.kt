package me.agaman.slackk.bot.impl

import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.newSingleThreadContext
import kotlinx.coroutines.experimental.runBlocking
import kotlinx.coroutines.experimental.sync.Mutex
import kotlinx.coroutines.experimental.sync.withLock
import mu.KotlinLogging

internal object AsyncExecutor {
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

    fun lockRun(mutex: Mutex, job: suspend () -> Unit) {
        runBlocking {
            mutex.withLock { job() }
        }
    }
}

