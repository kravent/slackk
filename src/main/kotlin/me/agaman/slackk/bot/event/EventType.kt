package me.agaman.slackk.bot.event

@Target(AnnotationTarget.CLASS)
annotation class EventType(
        val type: String,
        val subtype: String = "",
        val priority: Int = 0
)
