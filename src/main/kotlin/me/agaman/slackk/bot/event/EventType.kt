package me.agaman.slackk.bot.event

@Target(AnnotationTarget.CLASS)
annotation class EventType(
        val value: String,
        val priority: Int = 0
)
