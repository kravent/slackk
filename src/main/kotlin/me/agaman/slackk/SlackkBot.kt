package me.agaman.slackk

import kotlinx.coroutines.runBlocking
import me.agaman.slackk.bot.Bot
import me.agaman.slackk.bot.event.Event
import me.agaman.slackk.bot.request.PostMessageRequest
import me.agaman.slackk.bot.result.PostMessageResult
import me.agaman.slackk.bot.result.Result

abstract class SlackkBot(token: String) {
    protected val bot = Bot(token)

    init {
        bot.onStart(::onStart)
        bot.onAnyEvent(::onEvent)
    }

    protected open suspend fun onStart() {}
    protected open suspend fun onStop() {}
    protected open suspend fun onEvent(event: Event) {}

    fun run() {
        bot.start()
        runBlocking { onStop() }
    }

    protected suspend fun sendMessage(channel: String, text: String, asUser: Boolean = true) : Result<PostMessageResult> =
            bot.send(PostMessageRequest(channel = channel, text = text, asUser = asUser))
}
