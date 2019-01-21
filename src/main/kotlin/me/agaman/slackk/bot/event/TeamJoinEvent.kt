package me.agaman.slackk.bot.event

import me.agaman.slackk.bot.data.User

data class TeamJoinEvent(
        val user: User
) : Event()
