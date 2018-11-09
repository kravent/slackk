package me.agaman.slackk.bot.impl

import me.agaman.slackk.bot.event.Event
import me.agaman.slackk.bot.event.EventType
import me.agaman.slackk.bot.event.UnknownEvent
import me.agaman.slackk.bot.helper.Serializer
import org.reflections.Reflections

internal class BotApiHandler(
        token: String
) {
    private val apiEventListener = ApiEventListener(token)
    private val eventClassForType: Map<String, Class<out Event>> = Reflections("me.agaman.slackk")
            .getSubTypesOf(Event::class.java)
            .map { Pair(it, it.getAnnotation(EventType::class.java)) }
            .filter { (_, annotation) -> annotation != null }
            .groupBy { (_, annotation) -> EventTypeData.getEventTypeId(annotation) }
            .mapValues {(_, classes) -> classes.sortedBy { (_, annotation) -> annotation.priority }.last().first }

    private var startedListener: (() -> Unit)? = null
    private var eventListener: ((Event) -> Unit)? = null

    val user: String? get() = apiEventListener.user

    init {
        apiEventListener.onStarted(::handleStartEvent)
        apiEventListener.onMessage(::handleMessageEvent)
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
    }

    private fun handleStartEvent() {
        startedListener?.let { it() }
    }

    private fun handleMessageEvent(jsonEventData: String) {
        val type = Serializer.fromJson<EventTypeData>(jsonEventData)
        val event = eventClassForType[type.eventTypeId]?.let { Serializer.fromJson(jsonEventData, it) } ?: UnknownEvent(type.type, type.subtype, jsonEventData)
        eventListener?.let { it(event) }
    }

    private data class EventTypeData(
            val type: String,
            val subtype: String? = null
    ) {
        val eventTypeId: String get() = "$type:${subtype.orEmpty()}"

        companion object {
            fun getEventTypeId(annotation: EventType) = "${annotation.type}:${annotation.subtype}"
        }
    }
}
