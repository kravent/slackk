package me.agaman.slackk.bot.impl

import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.runBlocking
import kotlinx.coroutines.experimental.sync.Mutex
import me.agaman.slackk.bot.BotClient
import me.agaman.slackk.bot.request.RtmConnectRequest
import mu.KotlinLogging
import org.http4k.client.WebsocketClient
import org.http4k.core.Uri
import org.http4k.websocket.Websocket
import org.http4k.websocket.WsStatus
import java.util.concurrent.TimeUnit

private const val RECONNECTION_SLEEP_SECONDS = 10L

internal class ApiEventListener(
        token: String
) {
    private val botClient = BotClient(token)

    private var webSocket: Websocket? = null
    private var user: String? = null

    private var startedListener: (() -> Job)? = null
    private var messageListener: ((String) -> Job)? = null

    private val logger = KotlinLogging.logger {}

    val selfUser: String? get() = user

    fun onStarted(callback: () -> Unit) {
        startedListener = AsyncExecutor.wrapCallback(callback)
    }

    fun onMessage(callback: (String) -> Unit) {
        messageListener = AsyncExecutor.wrapCallback(callback)
    }

    fun start() {
        var connectionFinishedOk = false
        var reconnections = 0

        while (!connectionFinishedOk) {
            try {
                val mutex = Mutex(true)

                val rtmResult = botClient.send(RtmConnectRequest()).get()
                user = rtmResult.self.id

                val ws = WebsocketClient.nonBlocking(uri = Uri.of(rtmResult.url), onConnect = {
                    if (reconnections == 0)
                        startedListener?.let { it() }
                })
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

                webSocket = ws

                runBlocking { mutex.lock() }

                if (connectionFinishedOk)
                    break
            } catch (t: Throwable) {
                logger.error(t) { "WebSocket connection error" }
            }

            reconnections += 1
            logger.info { "Waiting $RECONNECTION_SLEEP_SECONDS seconds before WebSocket reconnection" }
            runBlocking { delay(RECONNECTION_SLEEP_SECONDS, TimeUnit.SECONDS) }
            logger.info { "Restarting WebSocket" }
        }

        webSocket = null
    }

    fun stop() {
        webSocket?.close(WsStatus.NORMAL)
    }

    private data class UrlResponse(
            val url: String
    )
}
