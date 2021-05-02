package me.shouheng.scheduler.dispatcher

import android.content.Context
import android.os.Handler
import android.os.Looper
import me.shouheng.scheduler.ISchedulerJob
import me.shouheng.scheduler.ThreadMode
import java.util.concurrent.Executor
import java.util.concurrent.atomic.AtomicInteger

/** The job for dispatcher. */
interface IDispatcherJob : Runnable {

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
    private val job: ISchedulerJob
): IDispatcherJob {

    private val parents = mutableListOf<IDispatcherJob>()
    private val children = mutableListOf<IDispatcherJob>()
    private var waiting = AtomicInteger(0)

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
            // TODO call `run` instead of execute on executor
            executor.execute(this)
        }
    }

    override fun run() {
        val realJob = {
            // Run the task.
            job.run(context)
            // Handle for children.
            children.forEach { it.notifyJobFinished(this) }
        }

        // Cases for main thread.
        if (job.threadMode() == ThreadMode.MAIN) {
            if (Thread.currentThread() == Looper.getMainLooper().thread) {
                realJob()
            } else {
                Handler(Looper.getMainLooper()).post { realJob() }
            }
        } else {
            // Cases for background thread.
            executor.execute { realJob() }
        }
    }
}
