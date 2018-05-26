package me.agaman.slackk.bot.impl

import com.github.shyiko.skedule.Schedule
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.cancelAndJoin
import kotlinx.coroutines.experimental.newSingleThreadContext
import kotlinx.coroutines.experimental.runBlocking
import java.util.concurrent.TimeUnit

internal class Scheduler {
    private val tasks: MutableList<Task> = mutableListOf()

    private val schedulerContext = newSingleThreadContext("slackk-scheduler")
    private val jobs: MutableList<Job> = mutableListOf()
    private var started = false

    fun addScheduler(schedule: Schedule, callback: () -> Unit) =
            addTask(ScheduledTask(schedule, Coroutines.wrapCallback(callback)))

    fun addTimer(interval: Long, intervalUnit: TimeUnit, callback: () -> Unit) =
            addTask(TimedTask(interval, intervalUnit, Coroutines.wrapCallback(callback)))

    fun start() {
        runBlocking(schedulerContext) {
            if (!started) {
                tasks.forEach(::startTask)
                started = true
            }
        }
    }

    fun stop() {
        runBlocking(schedulerContext) {
            jobs.forEach { it.cancelAndJoin() }
            jobs.clear()
            started = false
        }
    }

    private fun addTask(task: Task) {
        runBlocking(schedulerContext) {
            tasks += task
            if (started) startTask(task)
        }
    }

    private fun startTask(task: Task) {
        jobs += task.run(schedulerContext)
    }
}
