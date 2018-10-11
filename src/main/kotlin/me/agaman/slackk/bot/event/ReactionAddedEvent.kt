package me.agaman.slackk.bot.event

import me.agaman.slackk.bot.data.Item

@EventType("reaction_added")
data class ReactionAddedEvent(
        val user: String,
        val item: Item,
        val reaction: String,
        val item_user: String,
        val event_ts: String,
        val ts: String
) : Event()
