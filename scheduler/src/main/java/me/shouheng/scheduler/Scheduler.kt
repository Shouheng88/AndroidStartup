package me.shouheng.scheduler

import android.content.Context
import me.shouheng.scheduler.dispatcher.Dispatcher
import me.shouheng.scheduler.dispatcher.IDispatcher
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger

/** The scheduler */
class Scheduler {

    private var poolWorkQueue = LinkedBlockingQueue<Runnable>(128)

    /** All jobs for current scheduler */
    private val schedulerJobs = CopyOnWriteArrayList<ISchedulerJob>()

    /** The executor in which the background job will be invoked. */
    private var executor: Executor
    /** The logger. */
    private var logger: Logger
    /** The job dispatcher. */
    private var dispatcher: IDispatcher

    private val threadFactory: ThreadFactory = object : ThreadFactory {

        private val count = AtomicInteger(1)

        override fun newThread(r: Runnable): Thread {
            return Thread(r, "Scheduler #" + count.getAndIncrement())
        }
    }

    init {
        executor = ThreadPoolExecutor(
            CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_SECONDS, TimeUnit.SECONDS,
            poolWorkQueue, threadFactory)
        logger = StartupLogger
        dispatcher = Dispatcher
    }

    /** Set the custom executor. */
    fun setExecutor(executor: Executor): Scheduler {
        this.executor = executor
        return this
    }

    /** Set the logger. */
    fun setLogger(logger: Logger): Scheduler {
        this.logger = logger
        return this
    }

    /** To specify jobs for the scheduler. */
    fun <T : ISchedulerJob> jobs(vararg jobs: T): Scheduler {
        this.schedulerJobs.addAll(jobs)
        return this
    }

    /** Launch the scheduler. [context] here should be the global context. */
    fun launch(context: Context) {
        val jobs = mutableListOf<ISchedulerJob>()
        jobs.addAll(schedulerJobs)
        schedulerJobs.clear()
        dispatcher.dispatch(context, jobs, executor, logger)
    }

    companion object {
        private val CPU_COUNT = Runtime.getRuntime().availableProcessors()
        private val CORE_POOL_SIZE = 2.coerceAtLeast((CPU_COUNT - 1).coerceAtMost(4))
        private val MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1
        private const val KEEP_ALIVE_SECONDS = 30L

        fun newInstance() = Scheduler()
    }
}
