package me.agaman.slackk.bot.impl

import kotlinx.coroutines.experimental.ExecutorCoroutineDispatcher
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.cancelAndJoin
import kotlinx.coroutines.experimental.newSingleThreadContext
import kotlinx.coroutines.experimental.sync.Mutex
import java.util.concurrent.TimeUnit

internal class Scheduler {
    private val tasks: MutableList<Task> = mutableListOf()

    private val mutex = Mutex()
    private val parentJob = Job()
    private var schedulerContext: ExecutorCoroutineDispatcher? = null

    fun addScheduler(schedule: TimeZonedSchedule, callback: () -> Unit) =
            addTask(ScheduledTask(schedule, AsyncExecutor.wrapCallback(callback)))

    fun addTimer(interval: Long, intervalUnit: TimeUnit, callback: () -> Unit) =
            addTask(TimedTask(interval, intervalUnit, AsyncExecutor.wrapCallback(callback)))

    fun start() {
        AsyncExecutor.lockRun(mutex) {
            schedulerContext = newSingleThreadContext("slackk-scheduler")
            tasks.forEach { runTask(it) }
        }
    }

    fun stop() {
        AsyncExecutor.lockRun(mutex) {
            parentJob.cancelAndJoin()
            schedulerContext?.close()
            schedulerContext = null
        }
    }

    private fun addTask(task: Task) {
        AsyncExecutor.lockRun(mutex) {
            tasks += task
            runTask(task)
        }
    }

    private fun runTask(task: Task) {
        schedulerContext?.let { task.run(it + parentJob) }
    }
}
