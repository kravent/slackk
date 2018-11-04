package me.agaman.slackk.bot.impl

import com.github.shyiko.skedule.Schedule
import kotlinx.coroutines.experimental.delay
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.max

data class TimeZonedSchedule(
        private val schedule: Schedule,
        private val timeZoneId: ZoneId
) {
    fun iterator() = Iterator(schedule.iterate(ZonedDateTime.now(timeZoneId)), timeZoneId)
    fun next() = schedule.next(ZonedDateTime.now(timeZoneId))
    fun nextTimestamp() = next().toEpochSecond()

    class Iterator(
            private val iterator: Schedule.ScheduleIterator<ZonedDateTime>,
            private val timeZoneId: ZoneId
    ) {
        fun secondsUntilNext() = max(iterator.next().toEpochSecond() - ZonedDateTime.now(timeZoneId).toEpochSecond(), 0)
        suspend fun sleepUntilNext() {
            val sleepSeconds = secondsUntilNext()
            if (sleepSeconds > 0) {
                delay(TimeUnit.SECONDS.toMillis(sleepSeconds))
            }
        }
    }

    companion object {
        private val TIME_ZONED_SCHEDULE_REGEX = Regex("^(.*?)( in ([\\w-+/_]+))?$")

        fun parse(schedule: String): TimeZonedSchedule {
            return TIME_ZONED_SCHEDULE_REGEX.matchEntire(schedule)!!.let { match ->
                TimeZonedSchedule(
                        Schedule.parse(match.groupValues[1]),
                        match.groupValues[3].let {
                            when {
                                it.isEmpty() -> ZoneId.systemDefault()
                                else -> TimeZone.getTimeZone(it).toZoneId()
                            }
                        }
                )
            }
        }
    }
}
