package me.agaman.slackk.bot.impl

import com.github.shyiko.skedule.Schedule
import kotlinx.coroutines.experimental.Job
import java.time.ZonedDateTime
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit

internal class Scheduler {
    companion object {
        private const val CORE_POOL_SIZE = 1
    }

    private val executor = ScheduledThreadPoolExecutor(CORE_POOL_SIZE).apply {
        continueExistingPeriodicTasksAfterShutdownPolicy = false
        executeExistingDelayedTasksAfterShutdownPolicy = false
        removeOnCancelPolicy = true
    }
    private val scheduledTasks: MutableList<ScheduledTask> = mutableListOf()
    private val timedTasks: MutableList<TimedTask> = mutableListOf()
    private var started = false


    /**
     * Reference for schedule values: <a href="https://github.com/shyiko/skedule#format">skedule</a>.
     */
    fun addScheduler(schedule: String, callback: () -> Unit) {
        scheduledTasks += createScheduledTask(schedule, Coroutines.wrapCallback(callback))
    }

    fun addTimer(interval: Long, intervalUnit: TimeUnit, callback: () -> Unit) {
        timedTasks += TimedTask(interval, intervalUnit, Coroutines.wrapCallback(callback))
    }

    fun start() {
        if (!started) {
            scheduledTasks.forEach { startScheduledTask(it) }
            timedTasks.forEach { startTimer(it) }
            started = true
        }
    }

    fun stop() {
        executor.shutdown()
        started = false
    }


    private fun createScheduledTask(schedule: String, callback: () -> Job) : ScheduledTask {
        val task = ScheduledTask(Schedule.parse(schedule))
        task.callback = {
            scheduleNextTask(task)
            callback()
        }
        return task
    }

    private fun startScheduledTask(task: ScheduledTask) {
        task.scheduleIterator = task.schedule.iterate(now())
        scheduleNextTask(task)
    }

    private fun scheduleNextTask(task: ScheduledTask) {
        executor.schedule(task.callback!!, task.scheduleIterator!!.next().toEpochSecond() - now().toEpochSecond(), TimeUnit.SECONDS)
    }

    private fun startTimer(task: TimedTask) {
        executor.scheduleAtFixedRate({ task.callback() }, task.interval, task.interval, task.intervalUnit)
    }

    private fun now() = ZonedDateTime.now()

    private data class ScheduledTask(
            val schedule: Schedule,
            var scheduleIterator: Schedule.ScheduleIterator<ZonedDateTime>? = null,
            var callback: (() -> Unit)? = null
    )

    private data class TimedTask(
            val interval: Long,
            val intervalUnit: TimeUnit,
            val callback: () -> Job
    )
}
