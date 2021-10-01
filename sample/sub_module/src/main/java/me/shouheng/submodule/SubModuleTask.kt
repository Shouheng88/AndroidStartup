package me.shouheng.submodule

import android.content.Context
import android.util.Log
import me.shouheng.scheduler.ISchedulerJob
import me.shouheng.startup.annotation.StartupJob

/** Startup job run on submodule. Used to test component development. */
@StartupJob class SubModuleTask : ISchedulerJob {

    override fun dependencies(): List<String> = listOf("blocking")

    override fun run(context: Context) {
        Log.d("SubModuleTask", "runed ")
    }
}
