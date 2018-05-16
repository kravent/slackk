package me.agaman.slackk.bot

import com.google.gson.Gson
import me.agaman.slackk.bot.event.*
import me.agaman.slackk.bot.impl.ApiEventListener

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
        val type = gson.fromJson(jsonEventData, EventType::class.java).type
        return when (type) {
            "hello" -> gson.fromJson(jsonEventData, HelloEvent::class.java)
            "im_created" -> gson.fromJson(jsonEventData, ImCreatedEvent::class.java)
            "message" -> gson.fromJson(jsonEventData, MessageEvent::class.java)
            else -> UnknownEvent(type, jsonEventData)
        }
    }

    private data class EventType(
            val type: String
    )
}
