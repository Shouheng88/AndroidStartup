package me.shouheng.startupsample.jobs

import android.content.Context
import me.shouheng.scheduler.ISchedulerJob
import me.shouheng.scheduler.ThreadMode
import me.shouheng.startup.annotation.StartupJob
import me.shouheng.utils.ktx.toast
import me.shouheng.utils.stability.L

@StartupJob
class BlockingBackgroundJob : ISchedulerJob {

    override fun threadMode(): ThreadMode = ThreadMode.BACKGROUND

    override fun dependencies(): List<Class<out ISchedulerJob>> = emptyList()

    override fun run(context: Context) {
        Thread.sleep(5_000L) // 5 seconds
        L.d("BlockingBackgroundJob done! ${Thread.currentThread()}")
        toast("BlockingBackgroundJob done!")
    }
}
