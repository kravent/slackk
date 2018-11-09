package me.agaman.slackk.bot

import me.agaman.slackk.bot.event.Event
import me.agaman.slackk.bot.impl.AsyncExecutor
import me.agaman.slackk.bot.impl.BotApiHandler

class BotEventListener(
        token: String
) {
    private val botApiHandler = BotApiHandler(token)

    private var startListeners: MutableList<() -> Unit> = mutableListOf()
    private var eventListeners: MutableList<(Event) -> Unit> = mutableListOf()

    val selfUser get() = botApiHandler.user

    init {
        botApiHandler.onStarted { startListeners.forEach { job ->
            AsyncExecutor.safeRun("Error thrown in start listener") { job() } }
        }
        botApiHandler.onEvent { event ->
            eventListeners.forEach { job -> AsyncExecutor.safeRun("Error thrown in event listener") { job(event) } }
        }
    }

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

    fun start() = botApiHandler.start()
    fun stop() = botApiHandler.stop()
}
