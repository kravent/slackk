package me.agaman.slackk.bot.impl

import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.runBlocking
import me.agaman.slackk.bot.BotClient
import me.agaman.slackk.bot.request.RtmConnectRequest
import mu.KotlinLogging
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import java.util.concurrent.TimeUnit

internal class ApiEventListener(
        token: String
) {
    private val botClient = BotClient(token)
    private val httpClient = OkHttpClient()

    private var webSocket: WebSocket? = null
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
                val rtmResult = botClient.send(RtmConnectRequest()).get()
                user = rtmResult.self.id

                val request = Request.Builder()
                        .url(rtmResult.url)
                        .build()
                val listener = WebSocketListenerWrapper(
                        onOpen = { if (reconnections == 0) startedListener?.let { it() } },
                        onMessage = { text -> messageListener?.let { it(text) } },
                        onClosed = { reason -> logger.info { "Connection closed with reason: $reason" } },
                        onFailure = { t, _ -> throw t }
                )
                webSocket = httpClient.newWebSocket(request, listener)

                connectionFinishedOk = true
            } catch (t: Throwable) {
                logger.error(t) { "WebScket connection failure" }
                reconnections += 1

                runBlocking { delay(60, TimeUnit.SECONDS) }

                logger.info { "Restarting WebSocket 60 seconds after failure" }
            }
        }
    }

    fun stop() {
        webSocket?.close(1000, "Close")
    }

    private data class UrlResponse(
            val url: String
    )
}
