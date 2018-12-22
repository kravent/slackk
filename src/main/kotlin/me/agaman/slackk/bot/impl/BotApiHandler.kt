package me.agaman.slackk.bot.impl

import me.agaman.slackk.bot.event.Event
import me.agaman.slackk.bot.event.EventClassRepository
import me.agaman.slackk.bot.event.UnknownEvent
import me.agaman.slackk.bot.helper.Serializer

internal class BotApiHandler(
        token: String,
        asyncExecutor: AsyncExecutor = AsyncExecutor()
) {
    private val apiEventListener = ApiEventListener(token, asyncExecutor)
    private val pingPongHandler = PingPongHandler(asyncExecutor)

    private var startedListener: (() -> Unit)? = null
    private var eventListener: ((Event) -> Unit)? = null

    private var messageId: Long = 0

    val user: String? get() = apiEventListener.user

    init {
        apiEventListener.onStarted(::handleStartEvent)
        apiEventListener.onMessage(::handleMessageEvent)
        apiEventListener.onRestartStarted(::handleRestartStarted)
        apiEventListener.onRestartFinished(::handleRestartFinished)
        pingPongHandler.onPing(::handleSendPing)
        pingPongHandler.onTimeout(::handlePingPongTimeout)
    }

    fun onStarted(callback: () -> Unit) {
        startedListener = callback
    }

    fun onEvent(callback: (Event) -> Unit) {
        eventListener = callback
    }

    fun start() {
        apiEventListener.start()
    }

    fun stop() {
        apiEventListener.stop()
        pingPongHandler.stop()
    }

    private fun handleStartEvent() {
        startedListener?.let { it() }
        pingPongHandler.start()
    }

    private fun handleMessageEvent(jsonEventData: String) {
        val type = Serializer.fromJson<EventTypeData>(jsonEventData)
        if (type.type == "pong") {
            val pong = Serializer.fromJson<Pong>(jsonEventData)
            pingPongHandler.pong(pong.time)
        } else {
            val event = EventClassRepository.getClass(type.type, type.subtype)?.let { Serializer.fromJson(jsonEventData, it) }
                    ?: UnknownEvent(type.type, type.subtype, jsonEventData)
            eventListener?.let { it(event) }
        }
    }

    private fun handleRestartStarted() {
        pingPongHandler.stop()
    }

    private fun handleRestartFinished() {
        pingPongHandler.start()
    }

    private fun handleSendPing(time: Long) {
        apiEventListener.sendMessage(Serializer.toJson(Ping(id = messageId++, time = time)))
    }

    private fun handlePingPongTimeout() {
        pingPongHandler.stop()
        apiEventListener.restart()
    }

    private data class Ping(val id: Long, val type: String = "ping", val time: Long)
    private data class Pong(val time: Long)

    private data class EventTypeData(
            val type: String,
            val subtype: String? = null
    )
}
