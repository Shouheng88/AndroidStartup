package me.shouheng.startupsample.jobs

import android.content.Context
import me.shouheng.scheduler.ISchedulerJob
import me.shouheng.startup.annotation.StartupJob
import me.shouheng.utils.ktx.toast
import me.shouheng.utils.stability.L

/** The job is only running on process ":another" */
@StartupJob class AnotherProcessTargetJob : ISchedulerJob {

    override fun targetProcesses(): List<String> = listOf(":another")

    override fun dependencies(): List<String> = listOf(DependentBlockingBackgroundJob::class.java.name)

    override fun run(context: Context) {
        L.d("AnotherProcessTargetJob done!")
        toast("AnotherProcessTargetJob done!")
    }
}
