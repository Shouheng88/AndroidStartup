package me.shouheng.scheduler.dispatcher

import android.content.Context
import android.os.Handler
import android.os.Looper
import me.shouheng.scheduler.ISchedulerJob
import me.shouheng.scheduler.SchedulerException
import me.shouheng.scheduler.ThreadMode
import me.shouheng.scheduler.process.IProcessMatcher
import java.util.concurrent.Executor
import java.util.concurrent.atomic.AtomicInteger

/** The job for dispatcher. */
interface IDispatcherJob {

    /** Order of dispatcher job. @see [ISchedulerJob.priority] */
    fun order(): Int

    /** Execute the job. */
    fun execute()

    /** Add parent job. */
    fun addParent(job: IDispatcherJob)

    /** Add child job. */
    fun addChild(job: IDispatcherJob)

    /** Get parents. */
    fun parents(): List<IDispatcherJob>

    /** Get children. */
    fun children(): List<IDispatcherJob>

    /** For parent to notify children the job finished. */
    fun notifyJobFinished(job: IDispatcherJob)
}

/** The dispatcher job implementation. */
class DispatcherJob(
    private val context: Context,
    private val executor: Executor,
    private val matcher: IProcessMatcher,
    private val job: ISchedulerJob
): IDispatcherJob {

    private val parents = mutableListOf<IDispatcherJob>()
    private val children = mutableListOf<IDispatcherJob>()
    private var waiting = AtomicInteger(0)
    private val mainThreadHandler = Handler(Looper.getMainLooper())

    override fun order(): Int = job.priority()

    override fun execute() {
        val realJob = {
            // 1. Run the task if match given process.
            if (matcher.match(job.targetProcesses())) {
                job.run(context)
            }

            // 2. Then sort children task.
            children.sortBy { child -> -child.order() }

            // 3. No matter the task invoked in current process or not,
            // its children will be notified after that.
            children.forEach { it.notifyJobFinished(this) }
        }

        try {
            if (job.threadMode() == ThreadMode.MAIN) {
                // Cases for main thread.
                if (Thread.currentThread() == Looper.getMainLooper().thread) {
                    realJob.invoke()
                } else {
                    mainThreadHandler.post { realJob.invoke() }
                }
            } else {
                // Cases for background thread.
                executor.execute { realJob.invoke() }
            }
        } catch (e: Throwable) {
            throw SchedulerException(e)
        }
    }

    override fun addParent(job: IDispatcherJob) {
        parents.add(job)
        waiting.addAndGet(1)
    }

    override fun addChild(job: IDispatcherJob) {
        children.add(job)
    }

    override fun parents(): List<IDispatcherJob> = parents

    override fun children(): List<IDispatcherJob> = children

    override fun notifyJobFinished(job: IDispatcherJob) {
        if (waiting.decrementAndGet() == 0) {
            // All dependencies finished, commit the job.
            execute()
        }
    }
}
