package me.shouheng.startupsample.jobs

import android.content.Context
import android.os.Handler
import me.shouheng.scheduler.ISchedulerJob
import me.shouheng.startup.annotation.StartupJob
import me.shouheng.utils.ktx.toast
import me.shouheng.utils.stability.L

/** A job depend on blocking job. */
@StartupJob class DependentBlockingBackgroundJob : ISchedulerJob {

    override fun dependencies(): List<String> = listOf("blocking")

    override fun run(context: Context) {
        L.d("DependentBlockingBackgroundJob done! ${Thread.currentThread()}")
        Handler().post { toast("DependentBlockingBackgroundJob done! ${Thread.currentThread()}") }
    }
}
