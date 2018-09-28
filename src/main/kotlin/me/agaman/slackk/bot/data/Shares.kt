package me.agaman.slackk.bot.data

data class Shares(
        val public: Map<String, Share> = emptyMap(),
        val private: Map<String, Share> = emptyMap()
)
