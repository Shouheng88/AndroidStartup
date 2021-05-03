package me.shouheng.scheduler.dispatcher

import android.content.Context
import me.shouheng.scheduler.ISchedulerJob
import me.shouheng.scheduler.Logger
import me.shouheng.scheduler.SchedulerException
import java.util.concurrent.Executor

/** The job dispatcher. */
interface IDispatcher  {

    /** Do dispatch. */
    fun dispatch(
        context: Context,
        jobs: List<ISchedulerJob>,
        executor: Executor,
        logger: Logger
    )

}

/** The default job dispatcher. */
object Dispatcher : IDispatcher {

    private lateinit var globalContext: Context
    private lateinit var logger: Logger
    private lateinit var executor: Executor
    private lateinit var schedulerJobs: List<ISchedulerJob>

    /** Roots are those tasks don't depend on any other jobs. */
    private var roots = mutableListOf<IDispatcherJob>()

    override fun dispatch(
        context: Context,
        jobs: List<ISchedulerJob>,
        executor: Executor,
        logger: Logger
    ) {
        this.globalContext = context
        this.logger = logger
        this.schedulerJobs = jobs
        this.executor = executor

        // step 1: check circle.
        checkCycle()

        // step 2: prepare for dispatcher jobs.
        buildDispatcherJobs()

        // step 3: dispatch
        roots.forEach { it.execute() }
    }

    /** Check if there is a cycle. */
    private fun checkCycle() {
        val checking = mutableSetOf<Class<out ISchedulerJob>>()
        val checked = mutableSetOf<Class<out ISchedulerJob>>()
        val schedulerMap = mutableMapOf<Class<ISchedulerJob>, ISchedulerJob>()
        schedulerJobs.forEach { schedulerMap[it.javaClass] = it }
        schedulerJobs.forEach { schedulerJob ->
            checkCycleForJob(schedulerJob, schedulerMap, checking, checked)
        }
    }

    /** Check cycle for given job. */
    private fun checkCycleForJob(
        schedulerJob: ISchedulerJob,
        map: Map<Class<ISchedulerJob>, ISchedulerJob>,
        checking: MutableSet<Class<out ISchedulerJob>>,
        checked: MutableSet<Class<out ISchedulerJob>>
    ) {
        if (checking.contains(schedulerJob.javaClass)) {
            // Cycle detected.
            throw SchedulerException("Cycle detected for ${schedulerJob.javaClass.name}.")
        }
        if (!checked.contains(schedulerJob.javaClass)) {
            checking.add(schedulerJob.javaClass)
            if (schedulerJob.dependencies().isNotEmpty()) {
                schedulerJob.dependencies().forEach {
                    if (!checked.contains(it)) {
                        val job = map[it]!!
                        checkCycleForJob(job, map, checking, checked)
                    }
                }
            }
            checking.remove(schedulerJob.javaClass)
            checked.add(schedulerJob.javaClass)
        }
    }

    /** Build roots */
    private fun buildDispatcherJobs() {
        roots.clear()

        // Build the map from scheduler class type to dispatcher job.
        val map =  mutableMapOf<Class<ISchedulerJob>, DispatcherJob>()
        schedulerJobs.forEach {
            val dispatcherJob = DispatcherJob(this.globalContext, executor, it)
            map[it.javaClass] = dispatcherJob
        }

        // Fill the parent field for dispatcher job.
        schedulerJobs.forEach { schedulerJob ->
            val dispatcherJob = map[schedulerJob.javaClass]!!
            schedulerJob.dependencies().forEach {
                dispatcherJob.addParent(map[it]!!)
            }
        }

        // Fill the children field for dispatcher job.
        schedulerJobs.forEach { schedulerJob ->
            val dispatcherJob = map[schedulerJob.javaClass]!!
            dispatcherJob.parents().forEach {
                it.addChild(dispatcherJob)
            }
        }

        // Find roots.
        schedulerJobs.filter {
            it.dependencies().isEmpty()
        }.forEach {
            val dispatcherJob = map[it.javaClass]!!
            roots.add(dispatcherJob)
        }
    }

}
