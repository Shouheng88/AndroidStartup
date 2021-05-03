package me.shouheng.startupsample.jobs

import android.app.Application
import android.content.Context
import me.shouheng.scheduler.ISchedulerJob
import me.shouheng.scheduler.ThreadMode
import me.shouheng.startupsample.BuildConfig
import me.shouheng.uix.common.UIX
import me.shouheng.utils.stability.L
import me.shouheng.vmlib.VMLib

class ThirdPartLibrariesInitializeJob : ISchedulerJob {

    override fun threadMode(): ThreadMode = ThreadMode.MAIN

    override fun dependencies(): List<Class<out ISchedulerJob>> = emptyList()

    override fun run(context: Context) {
        VMLib.onCreate(context as Application)
        UIX.init(context)
        UIX.setDebug(BuildConfig.DEBUG)
        L.getConfig().setLogSwitch(BuildConfig.DEBUG)
        L.d("ThirdPartLibrariesInitializeJob done! ${Thread.currentThread()} ${System.currentTimeMillis()}")
    }
}
