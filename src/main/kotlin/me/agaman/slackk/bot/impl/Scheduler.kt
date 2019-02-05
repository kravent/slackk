package me.agaman.slackk.bot.impl

import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.TimeUnit

internal class Scheduler(
        private val asyncExecutor: AsyncExecutor = AsyncExecutor()
) {
    private val tasks: MutableList<Task> = mutableListOf()

    private val mutex = Mutex()
    private val parentJob = Job()
    private var executorDispatcher: ExecutorCoroutineDispatcher? = null

    private val schedulerContext get() = executorDispatcher?.let { it + CoroutineName("slackk-scheduler") }

    fun addScheduler(schedule: TimeZonedSchedule, callback: () -> Unit) =
            addTask(ScheduledTask(schedule, asyncExecutor.wrapCallback(callback)))

    fun addTimer(interval: Long, intervalUnit: TimeUnit, callback: () -> Unit) =
            addTask(TimedTask(interval, intervalUnit, asyncExecutor.wrapCallback(callback)))

    fun start() {
        lockRun(mutex) {
            executorDispatcher = AsyncExecutor.createDaemonSingleThreadExecutor().asCoroutineDispatcher()
            tasks.forEach { runTask(it) }
        }
    }

    fun stop() {
        lockRun(mutex) {
            parentJob.cancelAndJoin()
            executorDispatcher?.close()
            executorDispatcher = null
        }
    }

    private fun addTask(task: Task) {
        lockRun(mutex) {
            tasks += task
            runTask(task)
        }
    }

    private fun runTask(task: Task) {
        schedulerContext?.let { task.run(it + parentJob) }
    }

    private fun lockRun(mutex: Mutex, job: suspend () -> Unit) {
        runBlocking {
            mutex.withLock { job() }
        }
    }
}
