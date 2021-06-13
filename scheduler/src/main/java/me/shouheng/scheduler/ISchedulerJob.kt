package me.shouheng.scheduler

import android.content.Context

/** The job to run in scheduler. */
interface ISchedulerJob {

    /** The target process of the scheduler job. */
    fun targetProcess(): String = ""

    /** To specify what thread the job will be invoked. */
    fun threadMode(): ThreadMode

    /** Jobs the current job depend. */
    fun dependencies(): List<Class<out ISchedulerJob>>

    /** The business to run for current job. */
    fun run(context: Context)
}
