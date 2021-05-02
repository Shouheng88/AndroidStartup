package me.shouheng.startupsample.jobs

import android.content.Context
import me.shouheng.scheduler.ISchedulerJob
import me.shouheng.scheduler.ThreadMode
import me.shouheng.utils.ktx.toast
import me.shouheng.utils.stability.L

object DependentBlockingBackgroundJob : ISchedulerJob {

    override fun threadMode(): ThreadMode = ThreadMode.MAIN

    override fun dependencies(): List<Class<out ISchedulerJob>> = listOf(BlockingBackgroundJob.javaClass)

    override fun run(context: Context) {
        toast("DependentBlockingBackgroundJob done!")
        L.d("DependentBlockingBackgroundJob done! ${Thread.currentThread()}")
    }
}
