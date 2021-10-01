package me.shouheng.scheduler

import android.content.Context
import me.shouheng.scheduler.dispatcher.Dispatcher
import me.shouheng.scheduler.dispatcher.IDispatcher
import me.shouheng.scheduler.process.IProcessMatcher
import me.shouheng.scheduler.process.ProcessMatcherImpl
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.Executor

/** The scheduler */
class Scheduler constructor(builder: SchedulerBuilder) {

    /** All jobs for current scheduler */
    private val schedulerJobs = CopyOnWriteArrayList<ISchedulerJob>()

    /** The executor in which the background job will be invoked. */
    private var executor: Executor = DefaultExecutor.INSTANCE

    /** The logger. */
    private var logger: Logger = SchedulerLogger
    /** The job dispatcher. */
    private var dispatcher: IDispatcher
    private var matcher: IProcessMatcher? = null

    init {
        this.logger = builder.logger ?: logger
        this.executor = builder.executor ?: executor
        this.matcher = builder.matcher
        this.dispatcher = Dispatcher()
        this.schedulerJobs.addAll(builder.jobs)
    }

    /** Launch the scheduler. [context] here should be the global context. */
    fun launch(context: Context) {
        dispatcher.dispatch(
            context,
            schedulerJobs,
            executor,
            matcher ?: ProcessMatcherImpl,
            logger
        )
    }
}

@SchedulerDSL
class SchedulerBuilder {
    var executor: Executor? = null
    var logger: Logger? = null
    var matcher: IProcessMatcher? = null
    var jobs: MutableList<ISchedulerJob> = mutableListOf()
}

/** Create an scheduler by DSL. */
inline fun createScheduler(init: SchedulerBuilder.() -> Unit): Scheduler {
    val builder = SchedulerBuilder()
    builder.apply(init)
    return Scheduler(builder)
}

@DslMarker
annotation class SchedulerDSL
