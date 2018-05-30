package me.agaman.slackk.bot

import com.google.gson.Gson
import me.agaman.slackk.bot.event.Event
import me.agaman.slackk.bot.event.EventType
import me.agaman.slackk.bot.event.UnknownEvent
import me.agaman.slackk.bot.impl.ApiEventListener
import org.reflections.Reflections

class BotEventListener(
        token: String
) {
    companion object {
        private val gson = Gson()
    }

    private val apiEventListener = ApiEventListener(token)
    private val eventClassForType: Map<String, Class<out Event>> = Reflections("me.agaman.slackk")
            .getSubTypesOf(Event::class.java)
            .map { Pair(it, it.getAnnotation(EventType::class.java)) }
            .filter { (_, annotation) -> annotation != null }
            .groupBy { (_, annotation) -> getEventTypeId(annotation) }
            .mapValues {(_, classes) -> classes.sortedBy { (_, annotation) -> annotation.priority }.last().first }

    private var startListeners: MutableList<() -> Unit> = mutableListOf()
    private var eventListeners: MutableList<(Event) -> Unit> = mutableListOf()

    init {
        apiEventListener.onStarted { startListeners.forEach { it() } }
        apiEventListener.onMessage { jsonEventData ->
            val event = mapToEvent(jsonEventData)
            eventListeners.forEach { it(event) }
        }
    }


    val selfUser get() = apiEventListener.selfUser

    fun addStartListener(listener: () -> Unit) {
        startListeners.add(listener)
    }

    inline fun <reified T: Event> addEventListener(crossinline listener: (T) -> Unit) {
        addAnyEventListener { event ->
            if (event is T) {
                listener(event)
            }
        }
    }

    fun addAnyEventListener(listener: (Event) -> Unit) {
        eventListeners.add(listener)
    }

    fun start() = apiEventListener.start()
    fun stop() = apiEventListener.stop()


    private fun mapToEvent(jsonEventData: String) : Event {
        val type = gson.fromJson(jsonEventData, EventTypeData::class.java)
        return eventClassForType[type.eventTypeId]?.let { gson.fromJson(jsonEventData, it) } ?: UnknownEvent(type.type, type.subtype, jsonEventData)
    }

    private data class EventTypeData(
            val type: String,
            val subtype: String? = null
    ) {
        val eventTypeId: String get() = "$type:${subtype.orEmpty()}"
    }

    private fun getEventTypeId(annotation: EventType) =
            "${annotation.type}:${annotation.subtype}"
}
