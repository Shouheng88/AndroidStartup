package me.shouheng.startupsample.jobs

import android.content.Context
import me.shouheng.scheduler.ISchedulerJob
import me.shouheng.scheduler.ThreadMode
import me.shouheng.startup.annotation.StartupJob
import me.shouheng.utils.ktx.toast
import me.shouheng.utils.stability.L

@StartupJob
class AnotherProcessTargetJob : ISchedulerJob {

    override fun targetProcess(): String = ":another"

    override fun threadMode(): ThreadMode = ThreadMode.MAIN

    override fun dependencies(): List<Class<out ISchedulerJob>> = listOf(DependentBlockingBackgroundJob::class.java)

    override fun run(context: Context) {
        L.d("AnotherProcessTargetJob done!")
        toast("AnotherProcessTargetJob done!")
    }

}
