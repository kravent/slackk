package me.agaman.slackk

import me.agaman.slackk.bot.Bot
import me.agaman.slackk.bot.event.Event
import me.agaman.slackk.bot.request.PostMessageRequest
import me.agaman.slackk.bot.result.PostMessageResult
import me.agaman.slackk.bot.result.Result

abstract class SlackkBot(token: String) {
    protected val bot = Bot(token)

    init {
        bot.onStart { onStart() }
        bot.onAnyEvent { onEvent(it) }
    }

    protected open fun onStart() {}
    protected open fun onStop() {}
    protected open fun onEvent(event: Event) {}

    fun run() {
        bot.start()
        onStop()
    }

    protected fun sendMessage(channel: String, text: String, asUser: Boolean = true) : Result<PostMessageResult> =
            bot.send(PostMessageRequest(channel = channel, text = text, asUser = asUser))
}
