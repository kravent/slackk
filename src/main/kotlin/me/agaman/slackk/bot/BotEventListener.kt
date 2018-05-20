package me.agaman.slackk.bot

import com.google.gson.Gson
import me.agaman.slackk.bot.event.Event
import me.agaman.slackk.bot.event.EventType
import me.agaman.slackk.bot.event.UnknownEvent
import me.agaman.slackk.bot.impl.ApiEventListener
import org.reflections.Reflections
import org.reflections.util.ConfigurationBuilder

private val gson = Gson()

class BotEventListener(
        token: String
) {
    private val apiEventListener = ApiEventListener(token)

    val selfUser get() = apiEventListener.selfUser

    fun onStarted(listener: () -> Unit) {
        apiEventListener.onStarted(listener)
    }

    inline fun <reified T: Event> addListener(crossinline listener: (T) -> Unit) {
        addListenerForAnyEvent({ event ->
            if (event is T) {
                listener(event)
            }
        })
    }

    fun addListenerForAnyEvent(listener: (Event) -> Unit) {
        apiEventListener.addListener { listener(processEvent(it)) }
    }

    fun start() = apiEventListener.start()
    fun stop() = apiEventListener.stop()


    private fun processEvent(jsonEventData: String) : Event {
        val type = gson.fromJson(jsonEventData, EventTypeReader::class.java).type
        return Reflections(ConfigurationBuilder.build())
                .getSubTypesOf(Event::class.java)
                .filter { it.getAnnotation(EventType::class.java)?.value == type }
                .map { gson.fromJson(jsonEventData, it) }
                .firstOrNull() ?: UnknownEvent(type, jsonEventData)
    }

    private data class EventTypeReader(
            val type: String
    )
}
