package me.agaman.slackk.bot.event

import me.agaman.slackk.bot.data.User

@EventType("team_join")
class TeamJoinEvent(
        val user: User
) : Event()
