package me.shouheng.scheduler

import android.content.Context
import me.shouheng.scheduler.dispatcher.Dispatcher
import me.shouheng.scheduler.dispatcher.IDispatcher
import me.shouheng.scheduler.process.IProcessMatcher
import me.shouheng.scheduler.process.ProcessMatcherImpl
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.Executor

/** The scheduler */
class Scheduler constructor(
    executor: Executor? = null,
    logger: Logger? = null,
    matcher: IProcessMatcher? = null,
    jobs: List<ISchedulerJob> = mutableListOf()
) {

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
        this.logger = logger ?: this.logger
        this.executor = executor ?: this.executor
        this.matcher = matcher
        this.dispatcher = Dispatcher()
        this.schedulerJobs.addAll(jobs)
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

@SchedulerDSL class SchedulerBuilder {
    private var executor: Executor? = null
    private var logger: Logger? = null
    private var matcher: IProcessMatcher? = null
    private var jobs: List<ISchedulerJob> = mutableListOf()

    fun withExecutor(executor: Executor) {
        this.executor = executor
    }

    fun withLogger(logger: Logger) {
        this.logger = logger
    }

    fun withProcessMatcher(matcher: IProcessMatcher) {
        this.matcher = matcher
    }

    fun withJobs(jobs: MutableList<ISchedulerJob>) {
        this.jobs = jobs
    }

    internal fun build(): Scheduler {
        return Scheduler(executor, logger, matcher, jobs)
    }
}

/** Create an scheduler by DSL. */
fun createScheduler(init: SchedulerBuilder.() -> Unit): Scheduler {
    val builder = SchedulerBuilder()
    builder.apply(init)
    return builder.build()
}

@DslMarker annotation class SchedulerDSL
