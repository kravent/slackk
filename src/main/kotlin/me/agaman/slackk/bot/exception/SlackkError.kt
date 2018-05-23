package me.agaman.slackk.bot.exception

open class SlackkError : Exception {
    constructor() : super()
    constructor(message: String) : super(message)
}
