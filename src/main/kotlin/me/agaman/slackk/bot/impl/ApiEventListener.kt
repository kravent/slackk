package me.agaman.slackk.bot.impl

import me.agaman.slackk.bot.BotClient
import me.agaman.slackk.bot.request.RtmConnectRequest
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket

internal class ApiEventListener(
        token: String
) {
    private val botClient = BotClient(token)
    private val httpClient = OkHttpClient()
    private var webSocket: WebSocket? = null
    private var user: String? = null
    private var startedListener: (() -> Unit)? = null
    private var messageListener: ((String) -> Unit)? = null

    val selfUser: String? get() = user

    fun onStarted(listener: () -> Unit) {
        startedListener = addErrorHandler(listener)
    }

    fun onMessage(listener: (String) -> Unit) {
        messageListener = addErrorHandler(listener)
    }

    fun start() {
        val rtmResult = botClient.send(RtmConnectRequest()).get()
        user = rtmResult.self.id

        val request = Request.Builder()
                .url(rtmResult.url)
                .build()
        val listener = WebSocketListenerWrapper(
                onOpen = { startedListener?.let { it() } },
                onMessage = { text -> messageListener?.let { it(text) } }
        )
        webSocket = httpClient.newWebSocket(request, listener)
    }

    fun stop() {
        webSocket?.close(1000, "Close")
    }

    private data class UrlResponse(
            val url: String
    )
}
