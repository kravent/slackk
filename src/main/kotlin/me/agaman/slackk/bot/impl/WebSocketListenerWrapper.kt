package me.agaman.slackk.bot.impl

import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class WebSocketListenerWrapper(
        private var onOpen: (() -> Unit)? = null,
        private var onMessage: ((String) -> Unit)? = null,
        private var onClosing: (() -> Unit)? = null,
        private var onClosed: (() -> Unit)? = null,
        private var onFailure: (() -> Unit)? = null
) : WebSocketListener() {

    override fun onOpen(webSocket: WebSocket?, response: Response?) {
        onOpen?.invoke()
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        onMessage?.invoke(text)
    }

    override fun onClosing(webSocket: WebSocket?, code: Int, reason: String?) {
        onClosing?.invoke()
    }

    override fun onClosed(webSocket: WebSocket?, code: Int, reason: String?) {
        onClosed?.invoke()
    }

    override fun onFailure(webSocket: WebSocket?, t: Throwable?, response: Response?) {
        t?.let { throw it } // TODO remove debug line
        onFailure?.invoke()
    }
}
