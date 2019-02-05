package me.agaman.slackk.bot.impl

import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import mu.KotlinLogging
import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory

class AsyncExecutor {
    @PublishedApi
    internal val slackkCoroutineContext = createDaemonSingleThreadExecutor().asCoroutineDispatcher() + CoroutineName("slackk")
    @PublishedApi
    internal val logger = KotlinLogging.logger {}

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

    companion object {
        private val DAEMON_THREAD_FACTORY = ThreadFactory {
            Executors.defaultThreadFactory()
                    .newThread(it)
                    .apply { isDaemon = true }
        }

        fun createDaemonSingleThreadExecutor() = Executors.newSingleThreadExecutor(DAEMON_THREAD_FACTORY)
    }
}

