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

    private var startedListener: (suspend () -> Unit)? = null
    private var eventListener: (suspend (Event) -> Unit)? = null

    val user: String? get() = apiEventListener.user

    init {
        apiEventListener.onStarted{ handleStartEvent() }
        apiEventListener.onMessage { handleMessageEvent(it) }
    }

    fun onStarted(callback: suspend () -> Unit) {
        startedListener = callback
    }

    fun onEvent(callback: suspend (Event) -> Unit) {
        eventListener = callback
    }

    fun start() {
        apiEventListener.start()
    }

    fun stop() {
        apiEventListener.stop()
    }

    private suspend fun handleStartEvent() {
        startedListener?.let { it() }
    }

    private suspend fun handleMessageEvent(jsonEventData: String) {
        val type = Serializer.fromJson<EventTypeData>(jsonEventData)
        val event = EventClassRepository.getClass(type.type, type.subtype)?.let { Serializer.fromJson(jsonEventData, it) }
                ?: UnknownEvent(type.type, type.subtype, jsonEventData)
        eventListener?.let { it(event) }
    }

    private data class EventTypeData(
            val type: String,
            val subtype: String? = null
    )
}
