package me.agaman.slackk.bot.data

data class Shares(
        val public: Map<String, List<Share>> = emptyMap(),
        val private: Map<String, List<Share>> = emptyMap()
)
