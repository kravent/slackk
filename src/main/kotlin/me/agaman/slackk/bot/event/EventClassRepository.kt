package me.agaman.slackk.bot.event

import kotlin.reflect.KClass

object EventClassRepository {
    private val classMap: MutableMap<String, KClass<out Event>> = mutableMapOf()

    init {
        addEventClass(eventType = "hello", klass = HelloEvent::class)
        addEventClass(eventType = "user_change", klass = UserChangeEvent::class)
        addEventClass(eventType = "team_join", klass = TeamJoinEvent::class)
        addEventClass(eventType = "im_created", klass = ImCreatedEvent::class)
        addEventClass(eventType = "message", klass = MessageEvent::class)
        addEventClass(eventType = "message", eventSubtype = "message_changed", klass = MessageChangedEvent::class)
        addEventClass(eventType = "reaction_added", klass = ReactionAddedEvent::class)
    }

    fun addEventClass(eventType: String, eventSubtype: String? = null, klass: KClass<out Event>) {
        classMap["$eventType:${eventSubtype.orEmpty()}"] = klass
    }

    internal fun getClass(eventType: String, eventSubtype: String? = null): Class<out Event>? =
            classMap["$eventType:${eventSubtype.orEmpty()}"]?.java
}
