package me.shouheng.scheduler.dispatcher

import android.content.Context
import me.shouheng.scheduler.ISchedulerJob
import me.shouheng.scheduler.Logger
import me.shouheng.scheduler.SchedulerException
import me.shouheng.scheduler.process.IProcessMatcher
import java.util.concurrent.Executor

/** The job dispatcher. */
interface IDispatcher  {

    /** Do dispatch. */
    fun dispatch(
        context: Context,
        jobs: List<ISchedulerJob>,
        executor: Executor,
        matcher: IProcessMatcher,
        logger: Logger
    )

}

/** The default job dispatcher. */
class Dispatcher : IDispatcher {

    private lateinit var globalContext: Context
    private lateinit var logger: Logger
    private lateinit var executor: Executor
    private lateinit var schedulerJobs: List<ISchedulerJob>
    private lateinit var matcher: IProcessMatcher

    /** Roots are those tasks don't depend on any other jobs. */
    private var roots = mutableListOf<IDispatcherJob>()

    override fun dispatch(
        context: Context,
        jobs: List<ISchedulerJob>,
        executor: Executor,
        matcher: IProcessMatcher,
        logger: Logger
    ) {
        this.globalContext = context
        this.logger = logger
        this.schedulerJobs = jobs
        this.executor = executor
        this.matcher = matcher

        // step 1: check dependencies, cycle and miss.
        checkDependencies()

        // step 2: prepare for dispatcher jobs.
        buildDispatcherJobs()

        // step 3: dispatch
        roots.forEach { it.execute() }
    }

    /** Check if there is a cycle. */
    private fun checkDependencies() {
        val checking = mutableSetOf<String>()
        val checked = mutableSetOf<String>()
        val schedulerMap = mutableMapOf<String, ISchedulerJob>()
        schedulerJobs.forEach { schedulerMap[it.name()] = it }
        schedulerJobs.forEach { schedulerJob ->
            checkDependenciesReal(schedulerJob, schedulerMap, checking, checked)
        }
    }

    /** Check dependencies for given job: miss and cycle. */
    private fun checkDependenciesReal(
        schedulerJob: ISchedulerJob,
        map: Map<String, ISchedulerJob>,
        checking: MutableSet<String>,
        checked: MutableSet<String>
    ) {
        if (checking.contains(schedulerJob.name())) {
            // Cycle detected.
            throw SchedulerException("Cycle detected for ${schedulerJob.javaClass.name}.")
        }
        if (!checked.contains(schedulerJob.name())) {
            checking.add(schedulerJob.name())
            if (schedulerJob.dependencies().isNotEmpty()) {
                schedulerJob.dependencies().forEach {
                    if (!checked.contains(it)) {
                        val job = map[it] ?: throw SchedulerException(String.format("Dependency [%s] not found", it))
                        checkDependenciesReal(job, map, checking, checked)
                    }
                }
            }
            checking.remove(schedulerJob.name())
            checked.add(schedulerJob.name())
        }
    }

    /** Build roots */
    private fun buildDispatcherJobs() {
        roots.clear()

        // Build the map from scheduler job type to dispatcher job.
        val map =  mutableMapOf<String, DispatcherJob>()
        schedulerJobs.forEach {
            val dispatcherJob = DispatcherJob(this.globalContext, executor, matcher, it)
            if (map.containsKey(it.name())) {
                throw SchedulerException(String.format("Multiple jobs with same name: [%s]", it))
            }
            map[it.name()] = dispatcherJob
        }

        // Fill the parent field for dispatcher job.
        schedulerJobs.forEach { schedulerJob ->
            val dispatcherJob = map[schedulerJob.name()]!!
            schedulerJob.dependencies().forEach {
                if (!map.containsKey(it)) {
                    throw SchedulerException(String.format("Pre-positive job with name [%s] not found", it))
                }
                dispatcherJob.addParent(map[it]!!)
            }
        }

        // Fill the children field for dispatcher job.
        schedulerJobs.forEach { schedulerJob ->
            val dispatcherJob = map[schedulerJob.name()]!!
            dispatcherJob.parents().forEach {
                it.addChild(dispatcherJob)
            }
        }

        // Find roots.
        schedulerJobs.filter {
            it.dependencies().isEmpty()
        }.forEach {
            val dispatcherJob = map[it.name()]!!
            roots.add(dispatcherJob)
        }

        // Sort roots.
        roots.sortBy { -it.order() }
    }

}
