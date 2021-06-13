package me.shouheng.scheduler

import android.content.Context
import me.shouheng.scheduler.dispatcher.Dispatcher
import me.shouheng.scheduler.dispatcher.IDispatcher
import me.shouheng.scheduler.process.IProcessMatcher
import me.shouheng.scheduler.process.ProcessMatcherImpl
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger

/** The scheduler */
class Scheduler constructor(builder: SchedulerBuilder) {

    private var poolWorkQueue = LinkedBlockingQueue<Runnable>(128)

    /** All jobs for current scheduler */
    private val schedulerJobs = CopyOnWriteArrayList<ISchedulerJob>()

    /** The executor in which the background job will be invoked. */
    private var executor: Executor
    /** The logger. */
    private var logger: Logger = SchedulerLogger
    /** The job dispatcher. */
    private var dispatcher: IDispatcher
    private var matcher: IProcessMatcher? = null

    private val threadFactory: ThreadFactory = object : ThreadFactory {

        private val count = AtomicInteger(1)

        override fun newThread(r: Runnable): Thread {
            return Thread(r, "Scheduler #" + count.getAndIncrement())
        }
    }

    init {
        this.executor = ThreadPoolExecutor(
            CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_SECONDS, TimeUnit.SECONDS,
            poolWorkQueue, threadFactory)
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

    companion object {
        private val CPU_COUNT = Runtime.getRuntime().availableProcessors()
        private val CORE_POOL_SIZE = 2.coerceAtLeast((CPU_COUNT - 1).coerceAtMost(4))
        private val MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1
        private const val KEEP_ALIVE_SECONDS = 30L
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
