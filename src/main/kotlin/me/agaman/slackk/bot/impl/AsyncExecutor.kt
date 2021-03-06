package me.agaman.slackk.bot.impl

import kotlinx.coroutines.*
import mu.KotlinLogging
import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory
import kotlin.coroutines.CoroutineContext

class AsyncExecutor : CoroutineScope {
    override val coroutineContext: CoroutineContext = createDaemonSingleThreadExecutor().asCoroutineDispatcher() + CoroutineName("slackk")
    private val logger = KotlinLogging.logger {}

    inline fun wrapCallback(crossinline job: () -> Unit) = { safeLaunch { job() } }
    inline fun <reified T> wrapCallback(crossinline job: (T) -> Unit) = { param: T -> safeLaunch { job(param) } }

    inline fun safeLaunch(errorMessage: String = "Error thrown in Slackk callback", crossinline job: () -> Unit) =
            launch { safeRun(errorMessage) { job() } }

    fun safeRun(errorMessage: String = "Error thrown in Slackk callback", job: () -> Unit) {
        try {
            job()
        } catch (t: Throwable) {
            logger.error(t) { errorMessage }
        }
    }

    companion object {
        private val DAEMON_THREAD_FACTORY = ThreadFactory {
            Executors.defaultThreadFactory()
                    .newThread(it)
                    .apply { isDaemon = true }
        }

        fun createDaemonSingleThreadExecutor() = Executors.newSingleThreadExecutor(DAEMON_THREAD_FACTORY)
    }
}

