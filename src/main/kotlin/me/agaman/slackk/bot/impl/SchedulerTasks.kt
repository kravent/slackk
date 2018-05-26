package me.agaman.slackk.bot.impl

import com.github.shyiko.skedule.Schedule
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit
import kotlin.coroutines.experimental.CoroutineContext

internal interface Task {
    fun run(context: CoroutineContext): Job
}

internal class ScheduledTask(
        private val schedule: Schedule,
        private var callback: () -> Job
) : Task {
    override fun run(context: CoroutineContext) = launch(context) {
        val scheduleIterator = schedule.iterate(ZonedDateTime.now())
        while (isActive) {
            val sleepSeconds = scheduleIterator.next().toEpochSecond() - ZonedDateTime.now().toEpochSecond()
            if (sleepSeconds > 0) {
                delay(sleepSeconds, TimeUnit.SECONDS)
            }
            callback()
        }
    }
}

internal class TimedTask(
        private val interval: Long,
        private val intervalUnit: TimeUnit,
        private val callback: () -> Job
) : Task {
    override fun run(context: CoroutineContext) = launch(context) {
        while (isActive) {
            delay(interval, intervalUnit)
            callback()
        }
    }
}
