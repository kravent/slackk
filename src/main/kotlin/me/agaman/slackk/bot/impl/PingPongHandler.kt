package me.agaman.slackk.bot.impl

import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

internal class PingPongHandler(
        private val asyncExecutor: AsyncExecutor = AsyncExecutor()
) {
    private var eventLoopJob: Job? = null
    private var lastPingTime: Long = 0
    private var lastPongTime: Long = 0

    private var pingCallback: ((Long) -> Unit) = {}
    private var timeoutCallback: (() -> Unit) = {}

    fun onPing(callback: (Long) -> Unit) {
        pingCallback = callback
    }

    fun onTimeout(callback: () -> Unit) {
        timeoutCallback = callback
    }

    fun start() {
        lastPingTime = 0
        lastPongTime = 0
        eventLoopJob = asyncExecutor.launch { runEventLoop() }
    }

    fun stop() {
        eventLoopJob?.cancel()
    }

    fun pong(time: Long) {
        lastPongTime = time
    }

    private suspend fun runEventLoop() {
        while (true) {
            lastPingTime = currentTime()
            pingCallback(lastPingTime)

            delay(TimeUnit.SECONDS.toMillis(TIMEOUT))

            if (lastPongTime >= lastPingTime) {
                timeoutCallback()
                break
            }

            delay(TimeUnit.SECONDS.toMillis(WAIT_UNTIL_NEXT_PING))
        }
    }

    private fun currentTime() = System.currentTimeMillis() / 1000

    companion object {
        private const val TIMEOUT = 20L
        private const val WAIT_UNTIL_NEXT_PING = 10L
    }
}
