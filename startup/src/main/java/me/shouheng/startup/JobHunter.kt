package me.shouheng.startup

import me.shouheng.scheduler.ISchedulerJob

/** The job hunter. */
interface JobHunter {

    /** Hunt jobs by annotations. */
    fun hunt(): List<ISchedulerJob>
}
