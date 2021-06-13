package me.shouheng.startupsample.jobs

import android.content.Context
import android.os.Handler
import me.shouheng.scheduler.ISchedulerJob
import me.shouheng.scheduler.ThreadMode
import me.shouheng.startup.annotation.StartupJob
import me.shouheng.utils.ktx.toast
import me.shouheng.utils.stability.L

@StartupJob
class DependentBlockingBackgroundJob : ISchedulerJob {

    override fun threadMode(): ThreadMode = ThreadMode.MAIN

    override fun dependencies(): List<Class<out ISchedulerJob>> = listOf(BlockingBackgroundJob::class.java)

    override fun run(context: Context) {
        L.d("DependentBlockingBackgroundJob done! ${Thread.currentThread()}")
        Handler().post { toast("DependentBlockingBackgroundJob done! ${Thread.currentThread()}") }
    }
}
