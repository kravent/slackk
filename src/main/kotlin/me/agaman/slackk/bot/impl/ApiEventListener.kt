package me.agaman.slackk.bot.impl

import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import me.agaman.slackk.bot.BotClient
import me.agaman.slackk.bot.request.RtmConnectRequest
import mu.KotlinLogging
import org.http4k.client.WebsocketClient
import org.http4k.core.Uri
import org.http4k.websocket.Websocket
import org.http4k.websocket.WsMessage
import org.http4k.websocket.WsStatus
import java.time.Duration
import java.util.concurrent.TimeUnit

private const val RECONNECTION_SLEEP_SECONDS = 10L

internal class ApiEventListener(
        token: String,
        private val asyncExecutor: AsyncExecutor = AsyncExecutor()
) {
    private val botClient = BotClient(token)

    private var webSocket: Websocket? = null

    private var startedListener: (() -> Job)? = null
    private var messageListener: ((String) -> Job)? = null
    private var restartStartedListener: (() -> Unit)? = null
    private var restartFinishedListener: (() -> Unit)? = null

    private val logger = KotlinLogging.logger {}

    var user: String? = null
        private set

    fun onStarted(callback: () -> Unit) {
        startedListener = asyncExecutor.wrapCallback(callback)
    }

    fun onMessage(callback: (String) -> Unit) {
        messageListener = asyncExecutor.wrapCallback(callback)
    }

    fun onRestartStarted(callback: () -> Unit) {
        restartStartedListener = callback
    }

    fun onRestartFinished(callback: () -> Unit) {
        restartFinishedListener = callback
    }

    fun sendMessage(message: String) {
        webSocket?.send(WsMessage(message))
    }

    fun start() {
        var connectionFinishedOk = false
        var reconnections = 0

        while (!connectionFinishedOk) {
            try {
                val mutex = Mutex(true)

                val rtmResult = botClient.send(RtmConnectRequest()).get()
                user = rtmResult.self.id

                webSocket = WebsocketClient.nonBlocking(uri = Uri.of(rtmResult.url), timeout = Duration.ofSeconds(30), onConnect = { ws ->
                    if (reconnections == 0) {
                        startedListener?.let { it() }
                    } else {
                        restartFinishedListener?.let { it() }
                    }

                    ws.onMessage { wsMessage ->
                        messageListener?.let { it(wsMessage.bodyString()) }
                    }

                    ws.onClose { wsStatus ->
                        if (wsStatus == WsStatus.NORMAL) {
                            logger.info { "Connection closed with reason: $wsStatus" }
                            connectionFinishedOk = true
                        } else {
                            logger.error { "Connection closed with reason: $wsStatus" }
                        }
                        mutex.unlock()
                    }

                    ws.onError { t ->
                        logger.error(t) { "WebSocket error" }
                    }
                })

                runBlocking { mutex.lock() }

                if (connectionFinishedOk)
                    break
            } catch (t: Throwable) {
                logger.error(t) { "WebSocket connection error" }
            }

            restartStartedListener?.let { it() }

            reconnections += 1
            logger.info { "Waiting $RECONNECTION_SLEEP_SECONDS seconds before WebSocket reconnection" }
            runBlocking { delay(TimeUnit.SECONDS.toMillis(RECONNECTION_SLEEP_SECONDS)) }
            logger.info { "Restarting WebSocket" }
        }

        webSocket = null
    }

    fun stop() {
        webSocket?.close(WsStatus.NORMAL)
    }

    fun restart() {
        logger.info { "Forcing WebSocket restart" }
        webSocket?.close(WsStatus.PROTOCOL_ERROR)
    }
}
