package me.agaman.slackk.bot.impl

import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

internal class WebSocketListenerWrapper(
        private var onOpen: ((Response) -> Unit)? = null,
        private var onMessage: ((String) -> Unit)? = null,
        private var onClosing: ((String) -> Unit)? = null,
        private var onClosed: ((String) -> Unit)? = null,
        private var onFailure: ((Throwable, Response?) -> Unit)? = null
) : WebSocketListener() {

    override fun onOpen(webSocket: WebSocket, response: Response) {
        onOpen?.invoke(response)
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        onMessage?.invoke(text)
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        onClosing?.invoke(reason)
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        onClosed?.invoke(reason)
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        onFailure?.invoke(t, response)
    }
}
