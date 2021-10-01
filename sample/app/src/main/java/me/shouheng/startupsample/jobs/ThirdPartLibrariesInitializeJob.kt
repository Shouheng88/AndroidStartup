package me.shouheng.startupsample.jobs

import android.app.Application
import android.content.Context
import me.shouheng.scheduler.ISchedulerJob
import me.shouheng.startup.annotation.StartupJob
import me.shouheng.startupsample.BuildConfig
import me.shouheng.uix.common.UIX
import me.shouheng.utils.ktx.toast
import me.shouheng.utils.stability.L
import me.shouheng.vmlib.VMLib

/** Third part initialize job. */
@StartupJob class ThirdPartLibrariesInitializeJob : ISchedulerJob {

    override fun run(context: Context) {
        VMLib.onCreate(context as Application)
        UIX.init(context)
        UIX.setDebug(BuildConfig.DEBUG)
        L.getConfig().setLogSwitch(BuildConfig.DEBUG)
        L.d("ThirdPartLibrariesInitializeJob done! ${Thread.currentThread()} ${System.currentTimeMillis()}")
        toast("ThirdPartLibrariesInitializeJob done!")
    }
}
