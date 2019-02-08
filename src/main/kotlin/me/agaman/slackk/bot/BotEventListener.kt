package me.agaman.slackk.bot

import me.agaman.slackk.bot.event.Event
import me.agaman.slackk.bot.impl.AsyncExecutor
import me.agaman.slackk.bot.impl.BotApiHandler

class BotEventListener(
        token: String,
        private val asyncExecutor: AsyncExecutor = AsyncExecutor()
) {
    private val botApiHandler = BotApiHandler(token, asyncExecutor)

    private var startListeners: MutableList<suspend () -> Unit> = mutableListOf()
    private var eventListeners: MutableList<suspend (Event) -> Unit> = mutableListOf()

    val selfUser get() = botApiHandler.user

    init {
        botApiHandler.onStarted { startListeners.forEach { job ->
            asyncExecutor.safeLaunch("Error thrown in start listener") { job() } }
        }
        botApiHandler.onEvent { event ->
            eventListeners.forEach { job -> asyncExecutor.safeLaunch("Error thrown in event listener") { job(event) } }
        }
    }

    fun addStartListener(listener: suspend () -> Unit) {
        startListeners.add(listener)
    }

    inline fun <reified T: Event> addEventListener(crossinline listener: suspend (T) -> Unit) {
        addAnyEventListener { event ->
            if (event is T) {
                listener(event)
            }
        }
    }

    fun addAnyEventListener(listener: suspend (Event) -> Unit) {
        eventListeners.add(listener)
    }

    fun start() = botApiHandler.start()
    fun stop() = botApiHandler.stop()
}
