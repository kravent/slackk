package me.agaman.slackk

import me.agaman.slackk.bot.Bot
import me.agaman.slackk.bot.data.Message
import me.agaman.slackk.bot.event.Event
import me.agaman.slackk.bot.event.MessageChangedEvent
import me.agaman.slackk.bot.event.MessageEvent
import me.agaman.slackk.bot.request.PostMessageRequest
import me.agaman.slackk.bot.request.base.Request
import me.agaman.slackk.bot.result.PostMessageResult
import me.agaman.slackk.bot.result.Result
import java.util.concurrent.TimeUnit

class SlackkDsl(
        token: String
) {
    val bot = Bot(token)

    /**
     * Reference for schedule values: <a href="https://github.com/shyiko/skedule#format">skedule</a>.
     */
    fun schedule(schedule: String, task: suspend () -> Unit) = bot.schedule(schedule, task)
    fun onInterval(secondsInterval: Long, task: suspend () -> Unit) = bot.addTimer(secondsInterval, TimeUnit.SECONDS, task)

    fun onStart(listener: suspend () -> Unit) = bot.onStart(listener)
    inline fun <reified T : Event> onEvent(crossinline listener: suspend (T) -> Unit) = bot.onEvent(listener)
    fun onAnyEvent(listener: suspend (Event) -> Unit) = bot.onAnyEvent(listener)
    fun onUserMessage(listener: suspend (MessageEvent) -> Unit) = bot.onEvent<MessageEvent> {
        if(it.user != bot.selfUser)
            listener(it)
    }
    fun onUserMessageEdited(listener: suspend (oldMessage: Message, newMessage: Message) -> Unit) = bot.onEvent<MessageChangedEvent> {
        if (it.message.user != bot.selfUser)
            listener(it.previousMessage, it.message)
    }

    suspend inline fun <reified T : Any> send(request: Request<T>) : Result<T> = bot.send(request)
    suspend fun sendMessage(channel: String, text: String, asUser: Boolean = true) : Result<PostMessageResult> =
            bot.send(PostMessageRequest(channel = channel, text = text, asUser = asUser))

    fun start() = bot.start()
    fun stop() = bot.stop()
}

fun slackk(token: String, setup: SlackkDsl.() -> Unit) {
    val dsl = SlackkDsl(token)
    dsl.setup()
    dsl.start()
}
