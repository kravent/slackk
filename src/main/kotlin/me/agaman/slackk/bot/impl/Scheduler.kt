package me.agaman.slackk.bot.impl

import com.github.shyiko.skedule.Schedule
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.sync.Mutex
import kotlinx.coroutines.experimental.sync.withLock
import java.util.concurrent.TimeUnit

internal class Scheduler {
    private val tasks: MutableList<Task> = mutableListOf()

    private val mutex = Mutex()
    private val parentJob = Job()
    private var schedulerContext: ThreadPoolDispatcher? = null

    fun addScheduler(schedule: Schedule, callback: () -> Unit) =
            addTask(ScheduledTask(schedule, CallbackExecutor.wrapCallback(callback)))

    fun addTimer(interval: Long, intervalUnit: TimeUnit, callback: () -> Unit) =
            addTask(TimedTask(interval, intervalUnit, CallbackExecutor.wrapCallback(callback)))

    fun start() {
        lockRun {
            schedulerContext = newSingleThreadContext("slackk-scheduler")
            tasks.forEach { runTask(it) }
        }
    }

    fun stop() {
        lockRun {
            parentJob.cancelAndJoin()
            schedulerContext?.close()
            schedulerContext = null
        }
    }

    private fun addTask(task: Task) {
        lockRun {
            tasks += task
            runTask(task)
        }
    }

    private fun runTask(task: Task) {
        schedulerContext?.let { task.run(it + parentJob) }
    }

    private fun lockRun(job: suspend () -> Unit) {
        runBlocking {
            mutex.withLock { job() }
        }
    }
}
