package me.agaman.slackk.bot

import com.github.shyiko.skedule.Schedule
import me.agaman.slackk.bot.event.Event
import me.agaman.slackk.bot.impl.Scheduler
import me.agaman.slackk.bot.request.Request
import me.agaman.slackk.bot.result.Result
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
        eventListener.addStartListener { scheduler.start() }
    }

    inline fun <reified T : Any> send(request: Request<T>) : Result<T> = send(request, T::class)
    @PublishedApi
    internal fun <T : Any> send(request: Request<T>, resultClass: KClass<T>) : Result<T> = client.send(request, resultClass)

    fun onStart(listener: () -> Unit) = eventListener.addStartListener(listener)
    inline fun <reified T : Event> onEvent(crossinline listener: (T) -> Unit) = eventListener.addEventListener(listener)
    fun onAnyEvent(listener: (Event) -> Unit) = eventListener.addAnyEventListener(listener)

    /**
     * Reference for schedule values: <a href="https://github.com/shyiko/skedule#format">skedule</a>.
     */
    fun schedule(schedule: String, task: () -> Unit) = scheduler.addScheduler(Schedule.parse(schedule), task)
    fun addTimer(secondsInterval: Long, intervalUnit: TimeUnit, task: () -> Unit) = scheduler.addTimer(secondsInterval, intervalUnit, task)

    fun start() {
        eventListener.start()
    }
    fun stop() {
        scheduler.stop()
        eventListener.stop()
    }
}
