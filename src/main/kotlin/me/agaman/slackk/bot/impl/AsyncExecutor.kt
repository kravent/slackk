package me.agaman.slackk.bot.impl

import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import mu.KotlinLogging
import java.util.concurrent.Executors

internal object AsyncExecutor {
    private val slackkCoroutineContext = Executors.newSingleThreadExecutor().asCoroutineDispatcher() + CoroutineName("slackk")
    private val logger = KotlinLogging.logger {}

    inline fun wrapCallback(crossinline job: () -> Unit) = { runCallback(job) }
    inline fun <reified T> wrapCallback(crossinline job: (T) -> Unit) = { param: T -> runCallback { job(param) } }

    inline fun runCallback(crossinline job: () -> Unit) = GlobalScope.launch(slackkCoroutineContext) { safeRun { job() } }

    inline fun safeRun(errorMessage: String = "Error thrown in Slackk callback", crossinline job: () -> Unit) {
        try {
            job()
        } catch (t: Throwable) {
            logger.error(t) { errorMessage }
        }
    }

    fun lockRun(mutex: Mutex, job: suspend () -> Unit) {
        runBlocking {
            mutex.withLock { job() }
        }
    }
}

