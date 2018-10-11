package me.agaman.slackk.bot.event

import com.google.gson.annotations.SerializedName
import me.agaman.slackk.bot.data.Item

@EventType("reaction_added")
data class ReactionAddedEvent(
        val user: String,
        val item: Item,
        val reaction: String,
        @SerializedName("item_user")
        val itemUser: String,
        @SerializedName("event_ts")
        val eventTs: String,
        val ts: String
) : Event()
