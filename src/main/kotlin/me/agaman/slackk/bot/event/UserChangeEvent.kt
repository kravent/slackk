package me.agaman.slackk.bot.event

import me.agaman.slackk.bot.data.User

data class UserChangeEvent(
        val user: User
) : Event()
