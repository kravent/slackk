package me.agaman.slackk.bot.impl

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.websocket.WebSockets
import io.ktor.client.features.websocket.webSocket
import io.ktor.client.features.websocket.ws
import io.ktor.client.request.url
import io.ktor.http.Url
import io.ktor.http.cio.websocket.*
import io.ktor.http.fullPath
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import me.agaman.slackk.bot.BotClient
import me.agaman.slackk.bot.request.RtmConnectRequest
import mu.KotlinLogging
import java.net.URI
import java.time.Duration
import java.util.concurrent.TimeUnit

private const val RECONNECTION_SLEEP_SECONDS = 10L

internal class ApiEventListener(
        token: String,
        private val asyncExecutor: AsyncExecutor = AsyncExecutor()
) {
    private val botClient = BotClient(token)

    private val dispatcher = AsyncExecutor.createDaemonSingleThreadExecutor().asCoroutineDispatcher()
    private val client = HttpClient(CIO).config { install(WebSockets) }
    private var session: WebSocketSession? = null
    private var shouldStop = false

    private var startedListener: (() -> Job)? = null
    private var messageListener: ((String) -> Job)? = null

    private val logger = KotlinLogging.logger {}

    var user: String? = null
        private set

    fun onStarted(callback: () -> Unit) {
        startedListener = asyncExecutor.wrapCallback(callback)
    }

    fun onMessage(callback: (String) -> Unit) {
        messageListener = asyncExecutor.wrapCallback(callback)
    }

    fun start() = runBlocking(dispatcher) {
        var reconnections = 0
        shouldStop = false

        while (!shouldStop) {
            try {
                val rtmResult = botClient.send(RtmConnectRequest()).get()
                user = rtmResult.self.id

                client.ws(request = { url(rtmResult.url) }) {
                    try {
                        session = this

                        if (shouldStop) {
                            terminate()
                        } else {
                            pingIntervalMillis = Duration.ofSeconds(60).toMillis()

                            if (reconnections == 0) {
                                startedListener?.let { it() }
                            }

                            for (message in incoming) {
                                when (message) {
                                    is Frame.Text -> messageListener?.let { it(message.readText()) }
                                }
                            }
                        }
                    } catch (e: CancellationException) {
                        throw e
                    } catch (e: Throwable) {
                        logger.error(e) { "WebSocket error" }
                    } finally {
                        session = null
                    }
                }
            } catch (e: CancellationException) {
                logger.debug(e) { "Websocket execution cancelled" }
            } catch (e: Throwable) {
                logger.error(e) { "WebSocket connection error" }
            }

            if (!shouldStop) {
                reconnections += 1
                logger.info { "Waiting $RECONNECTION_SLEEP_SECONDS seconds before WebSocket reconnection" }
                delay(TimeUnit.SECONDS.toMillis(RECONNECTION_SLEEP_SECONDS))
                logger.info { "Restarting WebSocket" }
            }

        }

        logger.info { "WebSocket stopped" }
    }

    fun stop() = runBlocking(dispatcher) {
        shouldStop = true
        session?.terminate()
    }

}
