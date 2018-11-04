package me.agaman.slackk.bot.impl

import kotlinx.coroutines.experimental.*
import java.util.concurrent.TimeUnit
import kotlin.coroutines.experimental.CoroutineContext

internal interface Task {
    fun run(context: CoroutineContext): Job
}

internal class ScheduledTask(
        private val schedule: TimeZonedSchedule,
        private var callback: () -> Job
) : Task {
    override fun run(context: CoroutineContext) = GlobalScope.launch(context) {
        val scheduleIterator = schedule.iterator()
        while (coroutineContext.isActive) {
            scheduleIterator.sleepUntilNext()
            callback()
        }
    }
}

internal class TimedTask(
        private val interval: Long,
        private val intervalUnit: TimeUnit,
        private val callback: () -> Job
) : Task {
    override fun run(context: CoroutineContext) = GlobalScope.launch(context) {
        while (coroutineContext.isActive) {
            delay(intervalUnit.toMillis(interval))
            callback()
        }
    }
}
