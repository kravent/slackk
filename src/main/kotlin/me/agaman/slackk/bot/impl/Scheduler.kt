package me.agaman.slackk.bot.impl

import com.github.shyiko.skedule.Schedule
import kotlinx.coroutines.experimental.ThreadPoolDispatcher
import kotlinx.coroutines.experimental.cancel
import kotlinx.coroutines.experimental.newSingleThreadContext
import kotlinx.coroutines.experimental.runBlocking
import kotlinx.coroutines.experimental.sync.Mutex
import kotlinx.coroutines.experimental.sync.withLock
import java.util.concurrent.TimeUnit

internal class Scheduler {
    private val tasks: MutableList<Task> = mutableListOf()

    private val mutex = Mutex()
    private var schedulerContext: ThreadPoolDispatcher? = null

    fun addScheduler(schedule: Schedule, callback: () -> Unit) =
            addTask(ScheduledTask(schedule, Coroutines.wrapCallback(callback)))

    fun addTimer(interval: Long, intervalUnit: TimeUnit, callback: () -> Unit) =
            addTask(TimedTask(interval, intervalUnit, Coroutines.wrapCallback(callback)))

    fun start() {
        lockRun {
            schedulerContext = newSingleThreadContext("slackk-scheduler")
            tasks.forEach { it.run(schedulerContext!!) }
        }
    }

    fun stop() {
        lockRun {
            schedulerContext?.cancel()
            schedulerContext?.close()
            schedulerContext = null
        }
    }

    private fun addTask(task: Task) {
        tasks += task
        lockRun {
            schedulerContext?.let { task.run(it) }
        }
    }

    private fun lockRun(job: suspend () -> Unit) {
        runBlocking {
            mutex.withLock { job() }
        }
    }
}
