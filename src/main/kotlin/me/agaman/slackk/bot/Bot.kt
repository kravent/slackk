package me.agaman.slackk.bot

import me.agaman.slackk.bot.event.Event
import me.agaman.slackk.bot.impl.AsyncExecutor
import me.agaman.slackk.bot.impl.Scheduler
import me.agaman.slackk.bot.impl.TimeZonedSchedule
import me.agaman.slackk.bot.request.base.Request
import me.agaman.slackk.bot.result.Result
import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass

class Bot(
        token: String,
        asyncExecutor: AsyncExecutor = AsyncExecutor()
) {
    private val scheduler = Scheduler(asyncExecutor)

    @PublishedApi
    internal val eventListener = BotEventListener(token, asyncExecutor)
    @PublishedApi
    internal val client = BotClient(token)

    val selfUser get() = eventListener.selfUser

    inline fun <reified T : Any> send(request: Request<T>) : Result<T> = send(request, T::class)
    @PublishedApi
    internal fun <T : Any> send(request: Request<T>, resultClass: KClass<T>) : Result<T> = client.send(request, resultClass)

    fun onStart(listener: () -> Unit) = eventListener.addStartListener(listener)
    inline fun <reified T : Event> onEvent(crossinline listener: (T) -> Unit) = eventListener.addEventListener(listener)
    fun onAnyEvent(listener: (Event) -> Unit) = eventListener.addAnyEventListener(listener)

    /**
     * Reference for schedule values: <a href="https://github.com/shyiko/skedule#format">skedule</a>.
     */
    fun schedule(schedule: String, task: () -> Unit) = scheduler.addScheduler(TimeZonedSchedule.parse(schedule), task)
    fun addTimer(secondsInterval: Long, intervalUnit: TimeUnit, task: () -> Unit) = scheduler.addTimer(secondsInterval, intervalUnit, task)

    fun start() {
        scheduler.start()
        eventListener.start()
    }
    fun stop() {
        scheduler.stop()
        eventListener.stop()
    }
}
