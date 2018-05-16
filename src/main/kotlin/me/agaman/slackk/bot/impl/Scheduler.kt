package me.agaman.slackk.bot.impl

import com.github.shyiko.skedule.Schedule
import java.time.ZonedDateTime
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit

private val CORE_POOL_SIZE = 1

class Scheduler {
    private val executor = ScheduledThreadPoolExecutor(CORE_POOL_SIZE).apply {
        continueExistingPeriodicTasksAfterShutdownPolicy = false
        executeExistingDelayedTasksAfterShutdownPolicy = false
        removeOnCancelPolicy = true
    }
    private val scheduledTasks: MutableList<ScheduledTask> = mutableListOf()
    private val timedTasks: MutableList<TimedTask> = mutableListOf()
    private var started = false


    fun addScheduler(schedule: String, task: () -> Unit) {
        scheduledTasks += createScheduledTask(schedule, task)
    }

    fun addTimer(interval: Long, intervalUnit: TimeUnit, task: () -> Unit) {
        timedTasks += TimedTask(interval, intervalUnit, task)
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


    private fun createScheduledTask(schedule: String, job: () -> Unit) : ScheduledTask {
        val task = ScheduledTask(Schedule.parse(schedule))
        task.task = {
            scheduleNextTask(task)
            job()
        }
        return task
    }

    private fun startScheduledTask(task: ScheduledTask) {
        task.scheduleIterator = task.schedule.iterate(now())
        scheduleNextTask(task)
    }

    private fun scheduleNextTask(task: ScheduledTask) {
        executor.schedule(task.task!!, task.scheduleIterator!!.next().toEpochSecond() - now().toEpochSecond(), TimeUnit.SECONDS)
    }

    private fun startTimer(task: TimedTask) {
        executor.scheduleAtFixedRate(task.task, task.interval, task.interval, task.intervalUnit)
    }

    private fun now() = ZonedDateTime.now()

    private data class ScheduledTask(
            val schedule: Schedule,
            var scheduleIterator: Schedule.ScheduleIterator<ZonedDateTime>? = null,
            var task: (() -> Unit)? = null
    )

    private data class TimedTask(
            val interval: Long,
            val intervalUnit: TimeUnit,
            val task: () -> Unit
    )
}
