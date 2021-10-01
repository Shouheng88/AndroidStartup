package me.shouheng.scheduler

import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger

/** The default implementation for thread executor. */
class DefaultExecutor private constructor(
    corePoolSize: Int,
    maximumPoolSize: Int,
    keepAliveTime: Long,
    unit: TimeUnit,
    workQueue: BlockingQueue<Runnable>,
    threadFactory: ThreadFactory
): ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory) {

    companion object {
        private val CPU_COUNT = Runtime.getRuntime().availableProcessors()
        private val CORE_POOL_SIZE = 2.coerceAtLeast((CPU_COUNT - 1).coerceAtMost(4))
        private val MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1
        private const val KEEP_ALIVE_SECONDS = 30L

        private var poolWorkQueue = LinkedBlockingQueue<Runnable>(128)

        private val threadFactory: ThreadFactory = object : ThreadFactory {

            private val count = AtomicInteger(1)

            override fun newThread(r: Runnable): Thread {
                return Thread(r, "Scheduler #" + count.getAndIncrement())
            }
        }

        val INSTANCE by lazy {
            DefaultExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_SECONDS, TimeUnit.SECONDS,
                poolWorkQueue, threadFactory)
        }
    }
}
