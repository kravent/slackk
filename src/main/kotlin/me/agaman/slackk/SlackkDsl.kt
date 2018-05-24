package me.agaman.slackk

import me.agaman.slackk.bot.Bot
import me.agaman.slackk.bot.event.Event
import me.agaman.slackk.bot.event.MessageEvent
import me.agaman.slackk.bot.request.PostMessageRequest
import me.agaman.slackk.bot.request.Request
import me.agaman.slackk.bot.result.PostMessageResult
import me.agaman.slackk.bot.result.Result
import java.util.concurrent.TimeUnit

class SlackkDsl(
        token: String
) {
    val bot = Bot(token)

    fun schedule(schedule: String, task: () -> Unit) = bot.schedule(schedule, task)
    fun onInterval(secondsInterval: Long, task: () -> Unit) = bot.addTimer(secondsInterval, TimeUnit.SECONDS, task)

    fun onStart(listener: () -> Unit) = bot.onStart(listener)
    inline fun <reified T : Event> onEvent(crossinline listener: (T) -> Unit) = bot.onEvent(listener)
    fun onAnyEvent(listener: (Event) -> Unit) = bot.onAnyEvent(listener)
    fun onUserMessage(listener: (MessageEvent) -> Unit) = bot.onEvent<MessageEvent> {
        if(it.user != null && it.user != bot.selfUser)
            listener(it)
    }

    inline fun <reified T : Any> send(request: Request<T>) : Result<T> = bot.send(request)
    fun sendMessage(channel: String, text: String, asUser: Boolean = true) : Result<PostMessageResult> =
            bot.send(PostMessageRequest(channel = channel, text = text, asUser = asUser))

    fun start() = bot.start()
    fun stop() = bot.stop()
}

fun slackk(token: String, setup: SlackkDsl.() -> Unit) {
    val dsl = SlackkDsl(token)
    dsl.setup()
    dsl.start()
}
