package me.shouheng.scheduler

import android.content.Context
import android.support.annotation.IntRange

/** The job to run in scheduler. */
interface ISchedulerJob {

    /**
     * Specify a name for the job. It'll use the class name as default.
     * The job name was used, for example, to specify dependencies when
     * calling [dependencies] method. This is designed for cross module
     * (component).
     */
    fun name(): String = this.javaClass.name

    /**
     * Specify a priority of task. The priority is used when sorting tasks
     * having same parent or root tasks. The range of priority is from 0
     * to 100. The bigger the priority is the early it runs.
     */
    @IntRange(from = 0, to = 100) fun priority(): Int = 0

    /**
     * The target processes of the scheduler job. Now you are able to
     * specify multiple processes or empty list by default for task running
     * on all processes.
     */
    fun targetProcesses(): List<String> = emptyList()

    /** To specify what thread the job will be invoked. */
    fun threadMode(): ThreadMode = ThreadMode.MAIN

    /** Jobs the current job depend. */
    fun dependencies(): List<String> = emptyList()

    /** The business to run for current job. */
    fun run(context: Context)
}
