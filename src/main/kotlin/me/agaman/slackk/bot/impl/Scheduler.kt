package me.agaman.slackk.bot.impl

import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

internal class Scheduler {
    private val tasks: MutableList<Task> = mutableListOf()

    private val mutex = Mutex()
    private val parentJob = Job()
    private var executorDispatcher: ExecutorCoroutineDispatcher? = null

    private val schedulerContext get() = executorDispatcher?.let { it + CoroutineName("slackk-scheduler") }

    fun addScheduler(schedule: TimeZonedSchedule, callback: () -> Unit) =
            addTask(ScheduledTask(schedule, AsyncExecutor.wrapCallback(callback)))

    fun addTimer(interval: Long, intervalUnit: TimeUnit, callback: () -> Unit) =
            addTask(TimedTask(interval, intervalUnit, AsyncExecutor.wrapCallback(callback)))

    fun start() {
        AsyncExecutor.lockRun(mutex) {
            executorDispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
            tasks.forEach { runTask(it) }
        }
    }

    fun stop() {
        AsyncExecutor.lockRun(mutex) {
            parentJob.cancelAndJoin()
            executorDispatcher?.close()
            executorDispatcher = null
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
