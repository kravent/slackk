package me.agaman.slackk.bot

import me.agaman.slackk.bot.event.Event
import me.agaman.slackk.bot.impl.Scheduler
import me.agaman.slackk.bot.request.Request
import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass

class Bot(
        token: String
) {
    private val scheduler = Scheduler()

    @PublishedApi
    internal val eventListener = BotEventListener(token)
    @PublishedApi
    internal val client = BotClient(token)

    val selfUser get() = eventListener.selfUser

    init {
        eventListener.onStarted { scheduler.start() }
    }

    inline fun <reified T : Any> send(request: Request<T>) : T = send(request, T::class)
    @PublishedApi
    internal fun <T : Any> send(request: Request<T>, resultClass: KClass<T>) : T = client.send(request, resultClass)

    inline fun <reified T : Event> onEvent(crossinline listener: (T) -> Unit) = eventListener.addListener(listener)
    fun onAnyEvent(listener: (Event) -> Unit) = eventListener.addListenerForAnyEvent(listener)

    fun schedule(schedule: String, task: () -> Unit) = scheduler.addScheduler(schedule, task)
    fun addTimer(secondsInterval: Long, intervalUnit: TimeUnit, task: () -> Unit) = scheduler.addTimer(secondsInterval, intervalUnit, task)

    fun start() {
        eventListener.start()
    }
    fun stop() {
        scheduler.stop()
        eventListener.stop()
    }
}
